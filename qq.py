from __future__ import division
from sklearn.cross_validation import train_test_split
import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler

def dataTransform(filepath):






    numeric_cols = ['time_in_hospital', 'num_lab_procedures', 'num_procedures', 'num_medications', 'number_outpatient', 'number_emergency', 'number_inpatient', 'number_diagnoses']
    all[ numeric_cols ] = all[ numeric_cols ].astype(float)
    all[ numeric_cols ]=all[ numeric_cols ].apply(lambda x: MinMaxScaler().fit_transform(x))
    x_num_all = all[ numeric_cols ].as_matrix()

    # categorical
    x_cat_all = all.drop( numeric_cols + [ 'readmitted'], axis = 1 )


    fac_x_cat_all = pd.DataFrame()
    cat_cols = list(x_cat_all.columns.values)
    for col in cat_cols:
        all_cur, _ = pd.factorize(cat_all[col])
        fac_x_cat_all[col] = all_cur

    fac_x_cat_all = fac_x_cat_all.as_matrix()

    x_all = np.hstack(( x_num_all,  fac_x_cat_all))
    y_all = all.readmitted

    return x_all, y_all



path=r'noExpired.xlsx'
x_all,y_all=dataTransform(path)
x_train, x_test, y_train, y_test = train_test_split(x_all, y_all, test_size=.25,random_state=24)