import sys, getopt, subprocess, os, argparse, datetime
from BioProject import BioProject

class cron(object):
	"""docstring for cron"""
	def __init__(self, listFile, objMax, execute_command):
		self.listFile        = listFile
		self.option          = ''
		self.obj_max         = objMax
		self.execute_command = execute_command
		self.total_SRAs      = 0
		self.maximum         = 0

	def execute(self):
		os.chdir("/home/rdorado/Chlamy/Scripts/")
		GSEs = self.getListOfGSE()
		if not (self.option == 'N' or self.option == 'W'):
			print("Option Error: " + self.option)
			exit()

		to_execute = []
		file       = open(self.listFile,'w')
		file.write('Option: ' + self.option + "\n")
		file.write("ID\tTYPE\tpubmedid\tSTATUS\tNumber\n")
		for GSE in GSEs:
			if GSE['status'] != 'ok' and GSE['status'] != 'check':
				if GSE['TYPE'] == 's' or GSE['TYPE'] == 'g':
					if self.verifyToRunSra(GSE['Number']):
						to_execute.append(GSE)
						if self.execute_command:
							GSE['status'] = 'check'

					if GSE['Number'] == -1:
						GSE['Number'] = self.getNumberOfSRAa(GSE['ID'], GSE['TYPE'])

				else:
					GSE['status'] = 'error'

			file.write(GSE['ID']  + "\t" + GSE['TYPE'] + "\t" + GSE['pubmedid'] + "\t" + GSE['status'] + "\t" + str(GSE['Number']) + "\n")

		file.close()

		for gse in to_execute:
			command = "python3 DLRNAseq.py -" + gse['TYPE'] + " " + gse['ID']
			if gse['pubmedid'] != '':
				command += " --pubmedid " + gse['pubmedid']
				
			cmdline(command, self.execute_command) 

	def getNumberOfSRAa(self, ids, types):
		if types == 'g':
			bioProject = BioProject(ids, '', '')

		if types == 's':
			bioProject = BioProject('', ids, '')

		bioProject.verifyData()
		return bioProject.getNumberOfSRAs()

	def verifyToRunSra(self, number_sra):
		if (self.total_SRAs + number_sra) <= self.obj_max[self.option]:
			self.total_SRAs += number_sra
			return True

		if self.total_SRAs == 0 and self.option == 'W':
			self.total_SRAs += number_sra
			return True	

		if self.total_SRAs == 0 and self.option == 'N' and number_sra <= (self.obj_max[self.option] * 2):
			self.total_SRAs += number_sra
			return True			

		return False

	def getListOfGSE(self):
		GSEs = []
		with open(self.listFile) as allFile:
			for line in allFile:
				line = line.strip()
				if line.startswith("ID\t"):
					continue

				if line.startswith("Option"):
					cols = line.split(" ")
					self.option = 'N'
					if len(cols) > 1:
						self.option = cols[1]

					continue

				if line == "":
					continue

				cols            = line.split("\t")
				GSE             = {}
				GSE['ID']       = cols[0]
				GSE['TYPE']     = self.getIndexOfArray(cols, 1, '')
				GSE['pubmedid'] = self.getIndexOfArray(cols, 2, '')
				GSE['status']   = self.getIndexOfArray(cols, 3, '')
				GSE['Number']   = int(self.getIndexOfArray(cols, 4, -1))
				GSEs.append(GSE)

		return GSEs

	def getIndexOfArray(self, obj, i, default):
		if len(obj) > i:
			return obj[i]

		return default
		

'''
Execute a command in the linux terminal
@Param	{String}	command	Command to execute
@return	{String}			Result of the execution
'''
def cmdline(command, execute):
	print("--- Executing ---")
	print(command)
	if execute:
		process = subprocess.Popen(
			args  = command,
			shell = True
		)

		return process.communicate()[0]

def isEmpty(var):
	"""Verify if a variable is empty
	
	Verify if a variable is empty
	
	Arguments:
		var {all} -- Variable
	
	Returns:
		[boolean] -- If the variable is 'None' or '' or the length of it is 0
	"""
	return var == None or var == '' or len(var) == 0

now  = datetime.datetime.now()
date = now.strftime("%Y-%m-%d %H:%M")
print("----- INIT: " + date + " -----")
Cron = cron('/home/rdorado/Chlamy/Scripts/list_GSE_to_execute', {'N': 20, 'W': 100}, True)
Cron.execute()
print("--------------------------------------------------")