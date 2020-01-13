import sys, getopt, os

folder = "/home/rdorado/Chlamy/expression_values/"
#genes = ['Cre16.g666334.v5.5', 'Cre16.g666451.v5.5']
#genes = ['Cre02.g102276.v5.5', 'Cre02.g102302.v5.5']
genes = ['Cre10.g456420.v5.5', 'Cre10.g456460.v5.5']
expression_values = []

for root, dirs, files in os.walk(folder):  
	for filename in files:
		if filename == 'I-ExpressionValues-PRJNA373014.txt':
			expression_values = []
			with open(folder + filename) as file:
				new_file = open(folder + filename + '.new','w')
				for line in file:
					cols = line.strip().split('\t')
					options = "\t" + cols[1] + "\t" + cols[2] + "\t"

					if cols[0] == 'gene_id':
						for i in range(3,len(cols)):
							expression_values.append(0);

					if cols[0] in genes:
						for i in range(3,len(cols)):
							expression_values[i - 3] = expression_values[i - 3] + float(cols[i])

					else:
						new_file.write(line)


				#print(genes[0] + options + '\t'.join([str(e) for e in expression_values]))
				new_file.write(genes[0] + options + '\t'.join([str(round(e, 6)) for e in expression_values]) + '\n')
				new_file.close()