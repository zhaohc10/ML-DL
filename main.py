import pandas as pd

path=r'noExpired.xlsx'
all=pd.read_excel(path).dropna()

map = dict()
map['A'] = (1,139)
map['B'] = (140,239)
map['C'] = (240,279)
map['D'] = (280,289)
map['E'] = (290,319)
map['F'] = (320,359)
map['G'] = (360,389)
map['H'] = (390,459)
map['I'] = (460,519)
map['J'] = (520,579)
map['K'] = (580,629)
map['L'] = (630,679)
map['M'] = (680,709)
map['N'] = (710,739)
map['O'] = (740,759)
map['P'] = (760,779)
map['Q'] = (780,799)
map['R'] = (800,999)
map['X'] = "E"
map['Y'] = "V"

diagCol=['diag_1', 'diag_2','diag_3']
for i in diagCol:
    sd = all[i]
    for j in sd:
        diagStr=str(j)
        if 'E' in diagStr or 'V' in diagStr or '?' in diagStr:
            if 'E' in diagStr:
                sd[i] = 'X'
            elif 'V' in diagStr:
                sd[i] = 'Y'
        else:
            #print sd
            d = float(j)
            if d >= 1 and d <= 139 :
                sd[i] = 'A'
            elif d >= 140 and d <= 239 :
                sd[i] = 'B'
            elif d >= 240 and d <= 279 :
                sd[i] = 'C'
            elif d >= 280 and d <= 289 :
                sd[i] = 'D'
            elif d >= 290 and d <= 319 :
                sd[i] = 'E'
            elif d >= 320 and d <= 359 :
                sd[i] = 'F'
            elif d >= 360 and d <= 389 :
                sd[i] = 'G'
            elif d >= 390 and d <= 459 :
                sd[i] = 'H'
            elif d >= 460 and d <= 519 :
                sd[i] = 'I'
            elif d >= 520 and d <= 579 :
                sd[i] = 'J'
            elif d >= 580 and d <= 629 :
                sd[i] = 'K'
            elif d >= 630 and d <= 679 :
                sd[i] = 'L'
            elif d >= 680 and d <= 709 :
                sd[i] = 'M'
            elif d >= 710 and d <= 739 :
                sd[i] = 'N'
            elif d >= 740 and d <= 759 :
                sd[i] = 'O'
            elif d >= 760 and d <= 779 :
                sd[i] = 'P'
            elif d >= 780 and d <= 799 :
                sd[i] = 'Q'
            elif d >= 800 and d <= 999 :
                sd[i] = 'R'
            else:
                print d
                print "ERROR"
                exit(0);
    all[i]=sd
all['diag_1']