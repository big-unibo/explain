import pandas as pd
import unittest
from explain import *

class TestExplain(unittest.TestCase):

    def test_difference(self):
        foodmart_data = [
            ["Batteries", 95.08029105, 3320, 8478.78],
            ["Beer", 164.0013235, 27183, 28900.45],
            ["Canned Vegetables", 109.9642931, 5197, 11441.36],
            ["Cheese", 139.6043967, 7818, 18934.97],
            ["Chips", 96.38147239, 4178, 10076.44],
            ["Chocolate Candy", 87.07167507, 4331, 8294.05],
            ["Coffee", 97.75043662, 3492, 7876.64],
            ["Cookies", 145.4615444, 9300, 18349.83],
            ["Dried Fruit", 119.8094717, 7592, 16591.88],
            ["Eggs", 95.92059216, 4132, 9200.76],
            ["Fresh Fish", 40.56407859, 835, 1411.06],
            ["Fresh Fruit", 163.6739867, 11767, 25816.13],
            ["Fresh Vegetables", 204.5378084, 20734, 45172.32],
            ["Frozen Vegetables", 91.80096153, 4415, 9761.63],
            ["Lightbulbs", 94.67183128, 3459, 7686.35],
            ["Nasal Sprays", 56.45032637, 1813, 3300.54],
            ["Nuts", 90.2757498, 4400, 9269.02],
            ["Paper Wipes", 109.9180527, 5120, 11431.47],
            ["Preserves", 85.33767402, 4100, 7981.22],
            ["Sardines", 28.84833782, 819, 1357.8],
            ["Sliced Bread", 85.78467607, 3558, 7531.58],
            ["Soup", 131.3570338, 8006, 15966.1],
            ["Tuna", 51.66356854, 1710, 3210.76],
            ["Wine", 110.0952472, 5155, 10628.63]
        ]
        foodmart_df = pd.DataFrame(foodmart_data, columns=["Type", "Cost", "Quantity", "Revenue"])
        X, P, ax, fig, axe, fige = fit_all(foodmart_df, "Revenue", ["Quantity", "Cost"], plt_all=True)
        # fig.savefig('example.pdf')
        # fige.savefig('error.pdf')
        # print(P)
        self.assertTrue(foodmart_df.equals(X), X)
        self.assertTrue(P["model"].nunique() == 1)
        self.assertTrue(P["component"].nunique() == 2)
        self.assertTrue(P[(P["component"] == 'Cost') & (P["property"] == "degree")]["value"].iloc[0] == 2)

if __name__ == '__main__':
    unittest.main()
