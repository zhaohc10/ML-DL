
# coding: utf-8

import pandas as pd
# read excel file and return first 2 rows
path=r'Test.xlsx'
df=pd.read_excel(path)
df.head(3)



# filter the records
df1=df[df['discharge_disposition_id']!="Expired"]
# save the preProcess to the new file
writer_noExpired=pd.ExcelWriter('noExpired.xlsx',engine='xlsxwriter')
df1.to_excel(writer_noExpired,index=True,sheet_name='noExpired_diabetes.csv')
writer_noExpired.save()
# return the number of new file
num_rows=len(df1)
print "Number of no expired records is %d" %num_rows

