import pandas as pd
import unittest

from explain import *


class TestExplain(unittest.TestCase):
    foodmart_data = [
        ["Batteries", 95.08029105, 3320, 8478.78],  #
        ["Beer", 164.0013235, 27183, 28900.45],  #
        ["Canned Vegetables", 109.9642931, 5197, 11441.36],  #
        ["Cheese", 139.6043967, 7818, 18934.97],  #
        ["Chips", 96.38147239, 4178, 10076.44],  #
        ["Chocolate Candy", 87.07167507, 4331, 8294.05],  #
        ["Coffee", 97.75043662, 3492, 7876.64],  #
        ["Cookies", 145.4615444, 9300, 18349.83],  #
        ["Dried Fruit", 119.8094717, 7592, 16591.88],  #
        ["Eggs", 95.92059216, 4132, 9200.76],  #
        ["Fresh Fish", 40.56407859, 835, 1411.06],  #
        ["Fresh Fruit", 163.6739867, 11767, 25816.13],  #
        ["Fresh Vegetables", 204.5378084, 20734, 45172.32],  #
        ["Frozen Vegetables", 91.80096153, 4415, 9761.63],  #
        ["Lightbulbs", 94.67183128, 3459, 7686.35],  #
        ["Nasal Sprays", 56.45032637, 1813, 3300.54],  #
        ["Nuts", 90.2757498, 4400, 9269.02],  #
        ["Paper Wipes", 109.9180527, 5120, 11431.47],  #
        ["Preserves", 85.33767402, 4100, 7981.22],  #
        ["Sardines", 28.84833782, 819, 1357.8],  #
        ["Sliced Bread", 85.78467607, 3558, 7531.58],  #
        ["Soup", 131.3570338, 8006, 15966.1],  #
        ["Tuna", 51.66356854, 1710, 3210.76],  #
        ["Wine", 110.0952472, 5155, 10628.63]
    ]

    full_foodmart = \
        [
            ["Acetominifen", 800.1995, 1993.47, 26.28779772],  #
            ["Anchovies", 2296.38, 900, 56.92055926],  #
            ["Aspirin", 3613.39, 1599, 69.11147977],  #
            ["Auto Magazines", 1958.62, 809, 37.25629899],  #
            ["Bagels", 1872.97, 815, 53.27782342],  #
            ["Batteries", 8478.78, 3320, 82.08029105],  #
            # ["Beer", 28900.45, 27183, 171.0013235], #
            ["Bologna", 5859.95, 2588, 72.55031025],  #
            ["Candles", 1360.1, 815, 46.87953362],  #
            ["Canned Fruit", 3314.52, 1812, 63.57186813],  #
            ["Canned Vegetables", 11441.36, 5197, 109.9642931],  #
            ["Cereal", 6939.18, 3314, 91.30174068],  #
            ["Cheese", 18934.97, 7818, 134.6043967],  #
            ["Chips", 10076.44, 4178, 97.38147239],  #
            ["Chocolate", 1365.68, 802, 38.9551079],  #
            ["Chocolate Candy", 8294.05, 4331, 82.07167507],  #
            ["Clams", 1912.68, 882, 33.73419715],  #
            ["Cleaners", 4612.37, 2514, 72.91443146],  #
            ["Coffee", 7876.64, 3492, 90.75043662],  #
            ["Cold Remedies", 3356.71, 1776, 63.93712109],  #
            ["Computer Magazines", 1671.39, 846, 33.8826369],  #
            ["Conditioner", 1213.94, 813, 26.84164175],  #
            ["Cookies", 18349.83, 9300, 127.4615444],  #
            ["Cooking Oil", 6042.82, 3277, 70.73557744],  #
            ["Cottage Cheese", 3920.17, 1632, 65.61126097],  #
            ["Crackers", 3713.02, 1756, 50.93455506],  #
            ["Deli Meats", 7191.24, 3339, 78.80117924],  #
            ["Deli Salads", 4702.64, 2604, 62.57579748],  #
            ["Deodorizers", 2500.65, 927, 59.00649958],  #
            ["Dips", 7327.78, 3332, 76.60245324],  #
            ["Donuts", 5140.73, 2492, 65.69888423],  #
            ["Dried Fruit", 16591.88, 7592, 129.8094717],  #
            ["Dried Meat", 1755.16, 1007, 48.89462973],  #
            ["Eggs", 9200.76, 4132, 98.92059216],  #
            ["Fashion Magazines", 1716.4, 798, 31.42945812],  #
            ["Flavored Drinks", 5642.29, 2469, 83.11517823],  #
            ["French Fries", 5008.19, 2569, 62.76856647],  #
            ["Fresh Chicken", 2091.52, 878, 45.73313897],  #
            ["Fresh Fish", 1411.06, 835, 42.56407859],  #
            ["Fresh Fruit", 25816.13, 11767, 152.6739867],  #
            ["Fresh Vegetables", 45172.32, 20734, 212.5378084],  #
            ["Frozen Chicken", 6288.6, 2580, 80.30069357],  #
            ["Frozen Vegetables", 9761.63, 4415, 108.8009615],  #
            ["Gum", 1905.59, 850, 50.65306404],  #
            ["Hamburger", 3669.89, 1714, 63.57961703],  #
            ["Hard Candy", 4350.41, 1703, 70.95763792],  #
            ["Home Magazines", 1878.29, 932, 53.33924319],  #
            ["Hot Dogs", 5473.58, 2628, 73.98364684],  #
            ["Ibuprofen", 2363.7, 813, 55.61789794],  #
            ["Ice Cream", 6781.38, 2732, 80.34913479],  #
            ["Jam", 5401.81, 2556, 70.49700674],  #
            ["Jelly", 4609.61, 2565, 60.89410873],  #
            ["Juice", 6665.15, 3416, 86.64036992],  #
            ["Lightbulbs", 7686.35, 3459, 96.67183128],  #
            ["Maps", 1976.57, 967, 36.45863246],  #
            ["Milk", 7058.6, 4186, 91.01547477],  #
            ["Mouthwash", 3319.57, 1775, 57.61570966],  #
            ["Muffins", 7050.88, 3497, 80.96951828],  #
            ["Nasal Sprays", 3300.54, 1813, 59.45032637],  #
            ["Nuts", 9269.02, 4400, 101.2757498],  #
            ["Oysters", 1442.77, 708, 29.98381234],  #
            ["Pancake Mix", 1403.31, 814, 41.46077949],  #
            ["Pancakes", 1556.19, 783, 40.44857412],  #
            ["Paper Dishes", 4011.41, 1683, 62.33569294],  #
            ["Paper Wipes", 11431.47, 5120, 96.91805273],  #
            ["Pasta", 7033.53, 3195, 81.86614335],  #
            ["Peanut Butter", 5231.08, 2667, 71.32620549],  #
            ["Personal Hygiene", 6062.45, 3556, 69.86173643],  #
            ["Pizza", 6540.3, 3310, 84.87212128],  #
            ["Plastic Utensils", 6327.76, 2477, 89.54721868],  #
            ["Popcorn", 5125.49, 2510, 77.59252754],  #
            ["Popsicles", 6261.57, 3460, 73.13008278],  #
            ["Pot Cleaners", 3421.55, 1710, 62.49401679],  #
            ["Pot Scrubbers", 1934.99, 734, 50.98852123],  #
            ["Pots and Pans", 2719.6, 879, 43.14978428],  #
            ["Preserves", 7981.22, 4100, 98.33767402],  #
            ["Pretzels", 2029.49, 878, 53.04986127],  #
            ["Rice", 4713.84, 2064, 61.65741038],  #
            ["Sardines", 1357.8, 819, 27.84833782],  #
            ["Sauces", 1327.13, 781, 36.4297955],  #
            ["Screwdrivers", 1785.5, 810, 41.2551772],  #
            ["Shampoo", 5350.89, 2461, 83.14977785],  #
            ["Shellfish", 2398.08, 929, 38.97019502],  #
            ["Shrimp", 2146.49, 804, 44.33022771],  #
            ["Sliced Bread", 7531.58, 3558, 83.78467607],  #
            ["Soda", 6236.35, 3407, 87.97056414],  #
            ["Soup", 15966.1, 8006, 119.3570338],  #
            ["Sour Cream", 2894.05, 1652, 50.79637534],  #
            ["Spices", 4804.3, 2574, 66.31305793],  #
            ["Sponges", 1204.06, 882, 25.69956772],  #
            ["Sports Magazines", 1832.06, 909, 42.80257002],  #
            ["Sugar", 3272.44, 1725, 49.20524451],  #
            ["Sunglasses", 1500.11, 841, 32.73125353],  #
            ["TV Dinner", 5950.36, 2585, 73.1385766],  #
            ["Tofu", 1977.86, 886, 35.47313796],  #
            ["Toilet Brushes", 1147.27, 833, 35.87137434],  #
            ["Tools", 3627.02, 1682, 70.22474574],  #
            ["Toothbrushes", 1997.2, 836, 41.69004363],  #
            ["Tuna", 3210.76, 1710, 60.66356854],  #
            ["Waffles", 5655.97, 3407, 74.20618326],  #
            ["Wine", 10628.63, 5155, 101.0952472],  #
            ["Yogurt", 4759.66, 1783, 63.99028917]
        ]

    full_foodmart_df = pd.DataFrame(full_foodmart, columns=["Type", "Revenue", "Quantity", "Cost"])

    time_series = [
        [0.84, 0.91, 0.14, -0.76, -0.96, 0.34, 0.10],  #
        [0.91, 0.14, -0.76, -0.96, -0.28, 0.85, 0.20],  #
        [0.14, -0.76, -0.96, -0.28, 0.66, 0.69, 0.30],  #
        [-0.76, -0.96, -0.28, 0.66, 0.99, 0.08, 0.40],  #
        [-0.96, -0.28, 0.66, 0.99, 0.41, 0.59, 0.50],  #
        [-0.28, 0.66, 0.99, 0.41, -0.54, 0.57, 0.60],  #
        [0.66, 0.99, 0.41, -0.54, -1.00, 0.67, 0.70],  #
        [0.99, 0.41, -0.54, -1.00, -0.54, 0.63, 0.80],  #
        [0.41, -0.54, -1.00, -0.54, 0.42, 0.06, 0.90],  #
        [-0.54, -1.00, -0.54, 0.42, 0.99, 0.51, 1.00],  #
        [-1.00, -0.54, 0.42, 0.99, 0.65, 0.77, 1.10],  #
        [-0.54, 0.42, 0.99, 0.65, -0.29, 0.52, 1.20],  #
        [0.42, 0.99, 0.65, -0.29, -0.96, 0.52, 1.30],  #
        [0.99, 0.65, -0.29, -0.96, -0.75, 0.60, 1.40],  #
        [0.65, -0.29, -0.96, -0.75, 0.15, 0.78, 1.50],  #
        [-0.29, -0.96, -0.75, 0.15, 0.91, 0.90, 1.60],  #
        [-0.96, -0.75, 0.15, 0.91, 0.84, 0.17, 1.70],  #
        [-0.75, 0.15, 0.91, 0.84, -0.01, 0.31, 1.80],  #
        [0.15, 0.91, 0.84, -0.01, -0.85, 0.93, 1.90],  #
        [0.91, 0.84, -0.01, -0.85, -0.91, 0.29, 2.00],  #
    ]

    time_series_df = pd.DataFrame(time_series, columns=["t0","t1","t2","t3","t4","rnd","lin"])
 
    def test1(self): 
        foodmart_df = pd.DataFrame(self.foodmart_data, columns=["Type", "Cost", "Quantity", "Revenue"]) 
        X, P, ax, fig, axe, fige = fit_all(foodmart_df, "Revenue", ["Quantity", "Cost"], plt_all=True) 
        # fig.savefig('example.pdf') 
        # fige.savefig('error.pdf') 
        # print(P) 
        self.assertTrue(foodmart_df.equals(X), X) 
        self.assertTrue(P["model"].nunique() == 1) 
        self.assertTrue(P["component"].nunique() == 2) 
        self.assertTrue(P[(P["component"] == 'Cost') & (P["property"] == "degree")]["value"].iloc[0] == 2)

    def test2(self):
        full_foodmart_df = pd.DataFrame(self.full_foodmart, columns=["Type", "Revenue", "Quantity", "Cost"])
        X, P, ax, fig, axe, fige = fit_all(full_foodmart_df, "Revenue", ["Quantity", "Cost"], plt_all=True)
        self.assertTrue(full_foodmart_df.equals(X), X)
        self.assertTrue(P["model"].nunique() == 1)
        self.assertTrue(P["component"].nunique() == 2)
        self.assertTrue(P[(P["component"] == 'Cost') & (P["property"] == "degree")]["value"].iloc[0] == 2)

    def test3(self):
        df = pd.read_csv("gen_cube_1000000.csv")
        file_path = "../../../resources/intention/explain_scalability_python.csv"
        for j in range(0, 3):
            for i in range(2, 11):
                start = time.time()
                fit_all(df, "A", ["p({})".format(x) for x in range(1, i)])
                end_time = round((time.time() - start) * 1000)  # time is in ms
                pd.DataFrame([[i - 1, end_time]], columns=["measures", "time"]).to_csv(file_path, index=False, mode='a', header=not path.exists(file_path))

    def test4(self):
        X, P, ax, fig, axe, fige = multiple_regression_fit(self.full_foodmart_df, "Revenue", ["Quantity", "Cost"])
        self.assertTrue(self.full_foodmart_df.equals(X), X)
        self.assertTrue(P["model"].nunique() == 1)
        self.assertTrue(P["component"].nunique() == 1)

    def test5(self):
        X, P, ax, fig, axe, fige = multiple_regression_fit(self.full_foodmart_df, "Revenue", ["Cost"])
        self.assertTrue(self.full_foodmart_df.equals(X), X)
        self.assertTrue(P["model"].nunique() == 1)
        self.assertTrue(P["component"].nunique() == 1)

    def test6(self):
        X, P, ax, fig, axe, fige = time_series_fit(self.time_series_df, "t1", ["t0", "t1", "t2", "rnd", "lin"])
        self.assertTrue(self.time_series_df.equals(X), X)
        self.assertTrue(P["model"].nunique() == 1)
        self.assertTrue(P["component"].nunique() == 5)

    def test7(self):
        _, P, _, _, _, _ = time_series_fit(pd.read_csv('ft_by_timeid.csv'), "avgunitprice", ["avgunitcost"])
        self.assertTrue(P["model"].nunique() == 1)


if __name__ == '__main__':
    unittest.main()
