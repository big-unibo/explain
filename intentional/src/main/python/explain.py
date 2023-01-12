import argparse
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score, mean_squared_error
import json
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
import random
from sklearn.ensemble import IsolationForest

# SEED all random generators
seed = 4
random.seed(seed)
os.environ['PYTHONHASHSEED'] = str(seed)
np.random.seed(seed)

# ==============================================================================
# Chart variables
# ==============================================================================
titlesize = 16
subtitlesize = 14
labelsize = 14
axessize = 12
legendsize = 11
markersize = 5


def myprint(z, x='x'):
    c = str(round(z[0], 2))
    z = z[1:]
    ret = c + ('' if len(z) == 0 else ('$\cdot ' + x + '$' if len(z) == 1 else '$\cdot ' + x + '^' + str(len(z)) + '$') + ('' if myprint(z, x=x).startswith('-') else '+') + myprint(z, x=x))
    return ret


def fit(df, r=5, kpi='score', m_size=1, test_size=0.33, x_label='x', y_label='y', plt_all=False):  # test_size=0.33
    models = {}

    # filter away the outliers with isolation forests
    # clf = IsolationForest()
    # y = clf.fit_predict(df[[x_label, y_label]])
    # df = df[y == 1]

    # split the dataset into training and test
    if test_size is not None:
        train, test = train_test_split(df, test_size=test_size, random_state=seed)
        train = train.sort_values(by=x_label)
        test = test.sort_values(by=x_label)
    else:
        train = df
        test = df

    # create the figure
    fig, axs = plt.subplots(3, r, figsize=(4 * r, 3 * 3), sharey=True)
    fig2, axs2 = plt.subplots(1, 1, figsize=(4, 3))
    error_x = []
    error_y = []

    minscore = float('inf')
    minf, mtitle, argmin = None, None, None

    colors = ['red', 'blue', 'green', 'orange']
    # iterate over some polinomial degrees
    for i in range(0, r):
        # plot the raw data
        if plt_all:
            if r > 0:
                axs[0][i].scatter(df[x_label], df[y_label], s=m_size, c='black')
                axs[1][i].scatter(train[x_label], train[y_label], s=m_size, c='black')
                axs[2][i].scatter(test[x_label], test[y_label], s=m_size, c='black')
            else:
                axs[0][i].scatter(df[x_label], df[y_label], s=m_size, c='black', label='Raw')
                axs[1][i].scatter(train[x_label], train[y_label], s=m_size, c='black', label='Raw')
                axs[2][i].scatter(test[x_label], test[y_label], s=m_size, c='black', label='Raw')

        # fit the model
        z = np.polyfit(train[x_label], train[y_label], i)
        f = np.poly1d(z)

        # print it
        m = '{}={}'.format(y_label, myprint(list(z), x=x_label))
        color = colors[i % len(colors)]

        if plt_all:
            # ... and plot it
            x = [x for x in range(int(train[x_label].min()), int(train[x_label].max()), 1)]
            axs[1][i].plot(x, f(x), linewidth=2, label='p(' + str(i) + ')', c=color)
            # test it
            x = [x for x in range(int(test[x_label].min()), int(test[x_label].max()), 1)]
            axs[2][i].plot(x, f(x), linewidth=2, label='p(' + str(i) + ')', c=color)
            # In statistics, the coefficient of determination, denoted R2 or r2 and
            # pronounced "R squared", is the proportion of the variation in the
            # dependent variable that is predictable from the independent variable.
        interest = r2_score(test[y_label], f(test[x_label]))
        test_values = f(test[x_label])
        models[m] = {
            'r2': interest,
            'interest': interest,
            'mse': mean_squared_error(test[y_label], test_values),
            'score': mean_squared_error(test[y_label], test_values) * len(test) / (len(test) - i - 1),
            'degree': i,
            'equation': m,
            'coeff': [round(v, 2) for v in z]
        }
        error_x = error_x + [i]
        error_y = error_y + [models[m][kpi]]

        if models[m][kpi] < minscore:
            argmin = m
            minscore = models[m][kpi]

        # Add titles to the plots
        x = [x for x in range(int(df[x_label].min()), int(df[x_label].max()), 1)]

        if plt_all:
            ax0 = axs[i] if not plt_all else axs[0][i]
            ax0.plot(x, f(x), linewidth=2, label='p(' + str(i) + ')', c=color)
            ax0.set_ylabel(y_label, fontsize=labelsize)
            ax0.set_xlabel(x_label, fontsize=labelsize)
            ax0.grid(True)
            axs[2][i].legend()
            axs[1][i].set_title('Train', fontsize=titlesize)
            axs[2][i].set_title('Test', fontsize=titlesize)

    axs2.plot(error_x, error_y)
    axs2.set_xticks(list(range(0, r)))
    axs2.set_ylabel('$error$', fontsize=labelsize)
    axs2.set_xlabel('$degree$', fontsize=labelsize)
    axs2.grid(True)

    # fix the layout
    fig.tight_layout()
    fig2.tight_layout()
    fig.savefig('example_{}.pdf'.format(x_label))
    fig2.savefig('error_{}.pdf'.format(x_label))

    print(json.dumps(models, indent=2))
    print(argmin)
    return models[argmin], axs if not plt_all else axs[0], fig, axs2, fig2

def fit_all(X, measure, othermeasures, r=6, plt_all=False):
    if len(X) > 0:
        prop = []
        ax, fig, axe, fige = None, None, None, None
        for m in othermeasures:
            component, ax, fig, axe, fige = fit(X, r=r, kpi='score', x_label=m, y_label=measure, plt_all=plt_all)
            for k, v in component.items():
                prop.append(["Polyfit", m, k, v])
        if len(prop) == 0:
            raise ValueError('Empty properties with othermeasures ' + str(othermeasures))
        return X, pd.DataFrame(prop, columns=["model", "component", "property", "value"]), ax, fig, axe, fige
    else:
        raise ValueError('Empty data')

if __name__ == '__main__':
    ###############################################################################
    # PARAMETERS SETUP
    ###############################################################################
    parser = argparse.ArgumentParser()
    parser.add_argument("--path", help="where to put the output", type=str)
    parser.add_argument("--file", help="the file name", type=str)
    parser.add_argument("--session_step", help="the session step", type=int)
    parser.add_argument("--cube", help="cube")
    parser.add_argument("--measure", help="measure to explain")
    args = parser.parse_args()
    path = args.path.replace("\"", "")
    file = args.file
    measure = args.measure
    session_step = args.session_step
    cube = args.cube.replace("__", " ")
    cube = json.loads(cube)

    ###############################################################################
    # APPLY MODELS
    ###############################################################################
    try:
        X = pd.read_csv(path + file + "_" + str(session_step) + ".csv", encoding='cp1252')
    except Error:
        X = pd.read_csv(path + file + "_" + str(session_step) + ".csv", encoding="utf-8")

    import time
    import sys

    X.columns = [x.lower() for x in X.columns]
    print(X.columns)
    measures = [x["MEA"].lower() for x in cube["MC"]]
    if len(measures) < 2:
        raise ValueError("Not enough measures: " + str(measures))
    measures.remove(measure)
    start = time.time()
    X, P, ax, fig, axe, fige = fit_all(X, measure, measures, plt_all=True)
    end = time.time()
    print(round((end - start) * 1000))
    # sys.exit(1)
    P.to_csv(path + file + "_" + str(session_step) + "_property.csv", index=False)
