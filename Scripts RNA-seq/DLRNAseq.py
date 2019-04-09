import sys, getopt, subprocess, os, argparse, xmltodict
import urllib.request as urllib2
from BioProject import BioProject

class wrapp():
	"""docstring for wrapp"""
	def __init__(self, experimentName, shScript, experimentsFolder, numberOfSamples, PubMedId):
		self.experimentName                   = experimentName
		self.shScript                         = shScript
		self.experimentsFolder                = experimentsFolder
		self.numberOfSamples                  = numberOfSamples
		## 18/03/2019
		self.PubMedId                         = PubMedId
		
		self.file_ExperimentDescription       = 'C-ExperimentDescription-example.txt'
		#self.file_ExperimentDescription      = 'D-ExperimentDescription-example.txt'
		self.file_ExperimentDescriptionColumn = 'E-ExperimentDescriptionColumns'
		self.file_ExpressionValues            = 'F-ExpressionValues-'
		self.expressionValuesFolder           = self.experimentsFolder + 'expression_values/'
		
		self.actualPath                       = os.getcwd()

	def createNewFolder(self):
		self.expFolder = self.experimentsFolder + self.experimentName + '/'
		command        = 'mkdir ' + self.expFolder
		cmdline(command)

	def copyScripta(self):
		self.shSCriptNew  = self.expFolder + 'script_' + self.experimentName + '.sh'
		self.bbdukScript  = self.expFolder + 'script_' + self.experimentName + '_bbduk.sh'
		self.salmonScript = self.expFolder + 'script_' + self.experimentName + '_salmon.sh'
		command1          = 'cp ' + self.shScript  + ' ' + self.shSCriptNew
		command2          = 'cp bbduk.sh ' + self.bbdukScript
		command3          = 'cp salmon.sh ' + self.salmonScript
		command4		  = 'rm ' + self.shScript
		cmdline(command1)
		cmdline(command2)
		cmdline(command3)
		cmdline(command4)

	def changeToExpFolder(self):
		self.changeDirectory(self.expFolder)

	def backToScripts(self):
		self.changeDirectory(self.actualPath)

	def changeDirectory(self, folder):
		os.chdir(folder)

	def executeSH(self):
		command = 'sh ' + self.shSCriptNew
		cmdline(command)

	def executeBBduk(self):
		command = 'qsub ' + self.bbdukScript
		cmdline(command)

	def executeSalmon(self):
		command = 'qsub ' + self.salmonScript
		cmdline(command)

	def executeTximport(self):
		#command = 'echo "Rscript script_tximport.R one ' + self.experimentName + '" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o tximport.out -j y -N tximport -hold_jid salmon'
		#cmdline(command)
		command = 'Rscript script_tximport.R one ' + self.experimentName
		self.executeQsubommand(command, 'tximport', 'salmon')

	## 18/03/2019 ##
	
	def copyResultToExpressionVlauesFolder(self):
		command = 'cp tximportTranscriptomic/geneabundance_expression_values.txt ' + self.expressionValuesFolder + self.file_ExpressionValues + self.experimentName + '.txt'
		self.executeQsubommand(command, 'copyResult', 'tximport')

	def deleteUnusefulFiles(self):
		#command = 'rm -r SRR* *.po* *.pe* *.o* *.e* Clean/ Salmon/'
		command = 'rm -r SRR* *.po* *.pe* *.o* *.e* Clean/'
		self.executeQsubommand(command, 'remove', 'tximport')

	def getEsummary(self):
		command = 'python2 getEsummary.py -e ' + self.experimentName + ' -f ' + self.experimentsFolder + 'expression_values/' + self.file_ExperimentDescriptionColumn
		self.executeQsubommand(command, 'getEsummary', 'tximport')

	def executeQsubommand(self, command, name, hold):
		command = 'echo "' + command + '"| qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o ' + name + '.out -j y -N ' + name + ' -hold_jid ' + hold
		cmdline(command)

	def addExperimentToExperimentDescription(self, project):
		file = open(self.expressionValuesFolder + self.file_ExperimentDescription, "a")
		line = self.experimentName + "\t" + self.PubMedId + '\t' + project['Project_Acc'] + '\t' + project['Project_Title'] + '\t' + project['Project_Description']
		file.write(line + "\n")
		file.close()
	
	## 18/03/2019 ##

	def setNumberSRAinScripts(self):
		command = "sed -i -e 's/{sra number}/1-" + str(int(self.numberOfSamples)) + "/' "
		cmdline(command + self.bbdukScript)
		cmdline(command + self.salmonScript)

def executeDLRNAseq(gse, srp, experimentsFolder, pubmedid):
	bioProject      = BioProject(gse, srp, pubmedid)
	bioProject.verifyData()
	#exit()
	bioProject.writeNewShFile()
	experimentName  = bioProject.getExperimentName()
	script          = bioProject.getScript()
	numberOfSamples = bioProject.getNumberOfSamples()
	## 18/03/2019
	PubMedId        = bioProject.getPubMedId()
	project = bioProject.getProjectInfo()
	#data           = writeNewShFile(gse, srp)
	ex              = wrapp(experimentName, script, experimentsFolder, numberOfSamples, PubMedId)
	ex.createNewFolder()
	ex.copyScripta()
	ex.changeToExpFolder()
	ex.setNumberSRAinScripts()
	ex.executeSH()
	ex.executeBBduk()
	ex.executeSalmon()
	ex.backToScripts()
	ex.executeTximport()
	## 18/03/2019
	ex.getEsummary()
	ex.addExperimentToExperimentDescription(project)
	ex.changeToExpFolder()
	ex.copyResultToExpressionVlauesFolder()
	ex.deleteUnusefulFiles()

def getArguments():
	"""Get the arguments of the command executed
	
	Get the arguments and their values of the program execution
	
	Returns:
		Object -- The arguments with values
	"""
	parser = argparse.ArgumentParser()
	parser.add_argument("-g", "--gse", help = "The GSE accession")
	parser.add_argument("-s", "--srp", help = "The SRP accession or DRP accession")
	parser.add_argument("-f", "--experiments-folder", help = "The folder to set all the information")
	parser.add_argument("--pubmedid", help = "The id of the publication to use if there is no one")

	data   = parser.parse_args()

	if isEmpty(data.srp) and isEmpty(data.gse):
		print("Is required the GSE or SRP accession")
		parser.print_help()
		exit()

	if isEmpty(data.experiments_folder):
		data.experiments_folder = '/home/rdorado/Chlamy/'

	if not data.experiments_folder.endswith('/'):
		data.experiments_folder = data.experiments_folder + '/'

	return data

'''
Execute a command in the linux terminal
@Param	{String}	command	Command to execute
@return	{String}			Result of the execution
'''
def cmdline(command):
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

arguments = getArguments()
executeDLRNAseq(arguments.gse, arguments.srp, arguments.experiments_folder, arguments.pubmedid)

#python3 DLRNAseq.py -g GSE101944
#python3 DLRNAseq.py -s SRP113670