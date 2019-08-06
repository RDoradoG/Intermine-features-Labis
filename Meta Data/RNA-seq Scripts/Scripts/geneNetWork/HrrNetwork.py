import os, sys, getopt, argparse

from library import *
from HCCA import HCCA
from progressPercent import progressPercent
import numpy as np
from numpy import array, shape, sum, reshape, sqrt, dot, transpose

class app():
	"""docstring for app"""
	def __init__(self):
		arguments               = self.getArguments()
		self.progressPercent    = progressPercent(2)
		self.expressions_folder = arguments.expressions_folder
		self.prefix             = arguments.prefix
		self.max_hrr            = arguments.max_hrr
		self.steps              = arguments.steps
		self.min_cluster        = arguments.min_cluster
		self.max_cluster        = arguments.max_cluster
		self.size_islands       = arguments.size_islands
		self.cluster_ini        = arguments.cluster_ini
		self.island_ini        = arguments.island_ini
		self.lonely_ini       = arguments.lonely_ini

		self.files              = []
		self.genes              = {}
		self.resultFiles        = [
			'tmp.ranks',
			'tmp.hrr',
			'tmp.gene',
			'tmp.HCCA',
			arguments.result_file
		]
		np.seterr(divide='ignore', invalid='ignore')

	def getArguments(self):
		parser = argparse.ArgumentParser()
		parser.add_argument("-f", "--expressions_folder", help = "The rexpression matrix folder.")
		parser.add_argument("--prefix", default = '', help = "The Prefix of the files with the expresion matrix.")
		parser.add_argument("--max_hrr", type = int, default = 10,  help = "The number of nodes in the list of nodes per node.")
		parser.add_argument("-s", "--steps", type = int, default = 3,  help = "The number of max steps inside a cluster.")
		parser.add_argument("--min_cluster", type = int, default = 50,  help = "The number of min nodes inside a cluster.")
		parser.add_argument("--max_cluster", type = int, default = 250,  help = "The number of max nodes inside a cluster.")
		parser.add_argument("--size_islands", type = int, default = 25,  help = "The size of the islands.")
		parser.add_argument("--result_file", default = 'expressions.HCCA',  help = "The size of the islands.")

		parser.add_argument("--cluster_ini", type = int, default = 0,  help = "The inicial number of the clusters.")
		parser.add_argument("--island_ini", type = int, default = 0,  help = "The inicial number of the islands.")
		parser.add_argument("--lonely_ini", type = int, default = 0, help = "The inicial number of the lonely nodes.")

		data   = parser.parse_args()

		if isEmpty(data.expressions_folder):
			parser.print_help()
			exit()

		aux = data.expressions_folder.split('/')
		if not isEmpty(aux[len(aux) - 1]):
			data.expressions_folder += '/'

		return data

	def getExpressionsFolder(self):
		return self.expressions_folder

	def getFiles(self):
		return self.files

	def getFilesOfFolder(self):
		except_file = ['I-ExpressionValues-Miller_2010_Pro.txt']
		for root, dirs, files in os.walk(self.expressions_folder):
			for filename in files:
				if filename.startswith(self.prefix) and (filename not in except_file):
					self.files.append(filename)

	def getExpressionsMatrix(self):
		for file in self.files:
			with open(self.expressions_folder + file) as gff_file:
				first_line = True
				for line in gff_file:
					if first_line:
						first_line = False
						continue

					line = line.strip()
					cols = line.split("\t")
					key  = cols[0]
					if not key in self.genes:
						self.genes[key] = []

					for expression in cols[3:]:
						self.genes[key].append(float(expression))
	
	def printNumberConditions(self):
		for key in self.genes:
				print("number of conditions: " + str(len(self.genes[key])))
				break

	def calculateHRR(self):
		netDic   = {}
		indexa   = 0
		gene_ids = {}
		with open(self.resultFiles[0]) as rankFile:
			for line in rankFile:
				split            = line.rstrip().split("\t")
				topX             = split[1:self.max_hrr + 1]
				netDic[indexa]   = [topX,[]]
				gene_ids[indexa] = split[0]
				indexa           += 1

		genes    = list(netDic.keys())
		genes.sort()

		indexa    = 0
		hrr_file  = open(self.resultFiles[1], "a")
		gene_file = open(self.resultFiles[2], "a")
		self.progressPercent.setPreviousMesssage('Calculated HRR values for probeset: ')
		maximum = len(netDic)
		for j in genes:
			self.progressPercent.calculatePercent(indexa, maximum)
			currentGeneTop = netDic[int(j)][0]
			temp           = str(j) + '\t'
			indexa         += 1
			for k in range(len(currentGeneTop)):
				reverseCurrent = netDic[int(currentGeneTop[k])][0]
				try:
					test = 0
					test = reverseCurrent.index(str(j))
					maxa = max(test, k)
					temp += str(currentGeneTop[k]) + "+%s\t" % maxa
				except  ValueError:
					pass

			currentGeneBottom = netDic[j][1]
			gene_file.write(gene_ids[int(j)] + "\t" + str(j) + "\n")
			hrr_file.write(temp.rstrip() + "\n")

		self.progressPercent.finishPercent()
		hrr_file.close()
		gene_file.close()

	def calculateRanks(self):
		nominators   = []
		denominators = []
		genes        = []
		for key in self.genes:
			expRow    = array(self.genes[key])
			nominator = expRow - (sum(expRow) / len(expRow))
			nominators.append(nominator)
			denominators.append(sqrt(sum(nominator ** 2)))
			genes.append(key)

		nominators   = array(nominators)
		denominators = array(denominators)
		rank_file    = open(self.resultFiles[0], "a")
		i            = 0
		self.progressPercent.setPreviousMesssage('Calculated PCC values for probeset: ')
		maximum = len(nominators)
		for key in self.genes:
			self.progressPercent.calculatePercent(i + 1, maximum)
			dicto       = {}
			nominator   = dot(nominators, nominators[i])
			denominator = dot(denominators, denominators[i])
			rValues     = nominator / denominator
			for j in range(len(rValues)):
				dicto[rValues[j]] = str(j) + "\t"
			
			sort           = list(dicto.keys())
			sort.sort(reverse = True)
			temp           = []
			numOfProbesets = len(sort)
			for j in range(1,self.max_hrr + 1):
				temp.append(dicto[sort[j]])

			#for j in range(numOfProbesets - self.max_hrr, numOfProbesets):
			#	temp.append(dicto[sort[j]])

			rank_file.writelines([key + "\t"] + temp + ["\n"])
			i += 1

		self.progressPercent.finishPercent()
		rank_file.close()

	def writeGeneHCCAFile(self):
		genes  = {}
		with open(self.resultFiles[2]) as gene_file:
			for line in gene_file:
				cols           = line.strip().split("\t")
				genes[cols[1]] = cols[0]
		
		hcca_gene_file = open(self.resultFiles[4], "a")
		self.progressPercent.setPreviousMesssage('Calculated HRR values for probeset: ')
		i              = 0
		maximum        = len(genes)
		with open(self.resultFiles[3]) as gene_file:
			for line in gene_file:
				self.progressPercent.calculatePercent(i, maximum)
				i += 1
				cols     = line.strip().split("\t")
				gene     = genes[cols[0]]
				new_line = genes[cols[0]] + "\t" + cols[1] + "\n"
				hcca_gene_file.write(new_line)

		self.progressPercent.finishPercent()
		hcca_gene_file.close()

	def createFiles(self):
		for file in self.resultFiles:
			file_set = open(file, "w")
			file_set.close()

	def deleteFiles(self):
		i = 0
		for file in self.resultFiles:
			i += 1
			if i == 5:
				continue

			os.remove(file)

	def runHCCA(self):
		hcca = HCCA(self.resultFiles[1], self.resultFiles[3], self.steps, self.min_cluster, self.max_cluster, self.max_hrr, self.size_islands, self.cluster_ini, self.island_ini, self.lonely_ini)
		hcca.execute()
		
app = app()
app.createFiles()
app.getFilesOfFolder()
app.getExpressionsMatrix()
app.printNumberConditions()
app.calculateRanks()
app.calculateHRR()
app.runHCCA()
app.writeGeneHCCAFile()
#app.deleteFiles()