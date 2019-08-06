import sys
import re

keggFile = sys.argv[1]
keggNewFile = sys.argv[2]

regEx = "[.]t[0-9][.][0-9][.]v5[.]5"
changeRegEx = '.v5.5'

newFile = open(keggNewFile,"w") 

with open(keggFile) as keggFileText:
	for keggLine in keggFileText:
		keggLine = keggLine.replace("\n", '')
		columns = keggLine.split("\t")
		columns[0] = re.sub(regEx, changeRegEx,columns[0])
		newLine = "\t".join(columns) + "\n"
		newFile.write(newLine)
newFile.close() 