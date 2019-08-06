from progressPercent import progressPercent
from annotation_file import annotation_file
from bufferWritter import bufferWritter

from library import *

class eggNOG(object):
	"""docstring for eggNOG"""
	def __init__(self, arg, annotations):
		self.to_execute      = arg.eggNOG
		self.annotations     = annotations
		self.folder_name     = arg.folder_name + '/eggNOG'
		self.eggNOG_file     = self.folder_name + '/eggNOG.tab'
		self.bufferWritter   = bufferWritter(self.eggNOG_file)
		self.progressPercent = progressPercent(2)
		self.progressPercent.setPreviousMesssage('eggNOG: ')

	def execute(self):
		if self.to_execute:
			createFolder(self.folder_name)
			self.getFileData()

	def getFileData(self):
		line_num = 0
		for row in self.annotations['data']:
			line_num += 1
			eggNOGS  = row['eggNOG OGs'].split(',')
			egg_nog  = []
			for eggNOG in eggNOGS:
				parts = eggNOG.split('@')
				egg_nog.append(parts[0])

			self.printResult(row['gene id'], ','.join(egg_nog), row['eggNOG HMM Desc'])
			self.progressPercent.calculatePercent(line_num, self.annotations['lines'])

		self.bufferWritter.finishBuffering()
		self.progressPercent.finishPercent()

	def printResult(self, gene_id, eggNOGS, desc):
		line = gene_id + "\tEggNOG\t" + eggNOGS + "\t" + desc + "\n"
		self.bufferWritter.printLine(line)