import sys

gff3File = sys.argv[1]
gff3NewFile = sys.argv[2]


newFile = open(gff3NewFile,"w") 

with open(gff3File) as gffFileText:
	for gffLine in gffFileText:
		if gffLine.startswith('##'): 
			continue
		gffLine = gffLine.replace("\n", '')
		columns = gffLine.split("\t")
		features = columns[8].split(";")
		allFeatures = []
		for feature in features:
			keyValues = feature.split("=")
			if(keyValues[0] == 'Name'):
				keyValues[1] = keyValues[1] + ".v5.5"
			allFeatures.append("=".join(keyValues))
		columns[8] = ";".join(allFeatures)
		newLine = "\t".join(columns) + "\n"
		newFile.write(newLine)
newFile.close() 