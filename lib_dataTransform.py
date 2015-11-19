
# coding: utf-8

# # 02_03 Data Transformation
# 
# ## Import the initial dataset (without expired records)

# In[202]:

import pandas as pd

path=r'noExpired.xlsx'
all=pd.read_excel(path).dropna()


# ## Feature extraction from diag_1_desc,diag_2_desc,diag_3_desc using  "bag of words"
# ### Here, we use DataFrameMapper to vectorize a Pandas dataframe for sklearn usage!!

# In[203]:

from sklearn.feature_extraction.text import HashingVectorizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn_pandas import DataFrameMapper


cv=CountVectorizer(ngram_range=(1,3),strip_accents='unicode',stop_words='english')
hv=HashingVectorizer(stop_words='english',n_features=100)

"""
log_cols=['diag_1_desc','diag_2_desc','diag_3_desc']
for cols in log_cols:
    all[cols]=hv.fit_transform(all[cols].values)
#x_log_all = all[ log_cols ].as_matrix()
"""
log_cols=['diag_1_desc','diag_2_desc','diag_3_desc']
x_log_all = all[ log_cols ]
x_log_all.head(3)




# ## Convert numeric of dataframe to matrix for sklearn usage!!

# In[172]:

from sklearn.preprocessing import MinMaxScaler
from __future__ import division

numeric_cols = ['time_in_hospital', 'num_lab_procedures', 'num_procedures', 'num_medications', 'number_outpatient', 'number_emergency', 'number_inpatient', 'number_diagnoses']

all[ numeric_cols ] = all[ numeric_cols ].astype(float)
all[ numeric_cols ]=all[ numeric_cols ].apply(lambda x: MinMaxScaler().fit_transform(x))
x_num_all = all[ numeric_cols ].as_matrix()
x_num_all


# ## Convert categorical of dataframe to matrix  using factorize for sklearn usage!!

# In[215]:

# categorical
cat_all = all.drop( numeric_cols + [ 'readmitted'], axis = 1 )

fac_x_cat_all = pd.DataFrame()
cat_cols = list(cat_all.columns.values)
for col in cat_cols:
    all_cur, _ = pd.factorize(cat_all[col])
    fac_x_cat_all[col] = all_cur

fac_x_cat_all = fac_x_cat_all.as_matrix()


# ## Combine all the feature to be trained as x_all

# In[216]:

import numpy as np
x_all = np.hstack(( x_num_all,  fac_x_cat_all))


# In[217]:

x_all.shape


# In[218]:

y_all = all.readmitted


# In[220]:

from __future__ import division
def dataTransform(filepath):
    import pandas as pd
    import numpy as np

    from sklearn.preprocessing import MinMaxScaler



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


# In[ ]:



