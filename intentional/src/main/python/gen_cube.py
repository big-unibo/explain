import math
import numpy as np
import pandas as pd

points = 1000000

df = pd.DataFrame(np.random.randint(0, 100, size=(points, 1)), columns=list('A'))
for i in range(1, 10):
    df["p({})".format(i)] = df['A'].apply(lambda x: math.pow(x, 1.0 / i))

df.to_csv('gen_cube_{}.csv'.format(points))
