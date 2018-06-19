from bs4 import BeautifulSoup
import urllib2
import urllib
import sys
import os
#person
#buildings
#public buildings
#parks --for trees
#
link=["http://www.banq.qc.ca/collections/images/recherche/index.html?keyword=*&fonction=search&f_sous_collection_f=&tri=&nbResult=100&ajouterHistorique=oui&f_sujet_f=Personnes&page=",
"http://www.banq.qc.ca/collections/images/recherche/index.html?keyword=*&fonction=search&f_sous_collection_f=&tri=&nbResult=100&ajouterHistorique=oui&f_sujet_f=%C3%89difice&page=",
"http://www.banq.qc.ca/collections/images/recherche/index.html?keyword=*&fonction=search&f_sous_collection_f=&tri=&nbResult=100&ajouterHistorique=oui&f_sujet_f=B%C3%A2timents+publics&page=",
"http://www.banq.qc.ca/collections/images/recherche/index.html?keyword=*&fonction=search&f_sous_collection_f=&tri=&nbResult=100&ajouterHistorique=oui&f_sujet_f=Parcs&page="
]
folders=['person','building','building1','tree']
for j in range(1,3):
	f = open(folders[j]+'.txt','w')
	print folders[j]+str(j)
	i=1
	while 1: 
		try:
			soup=BeautifulSoup(urllib2.urlopen(link[j]+str(i)).read());
		except:
			e = sys.exc_info()[0]
			print str(j) +" \n"+e
		i=i+1
		hig=soup.find_all("a",class_="highslide")
		if len(hig)<10:
			break
		for hh in hig:
			h=hh.get("href")
			hhh=h.split('&')
			f.write(hhh[0]+'\n')
	f.close()
	print "pages:"+str(i)