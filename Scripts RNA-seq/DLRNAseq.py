import sys, getopt, subprocess, os, argparse
import urllib.request as urllib2
import xmltodict

class wrapp():
	"""docstring for wrapp"""
	def __init__(self, experimentName, shScript, experimentsFolder):
		self.experimentName    = experimentName
		self.shScript          = shScript
		self.experimentsFolder = experimentsFolder
		self.actualPath        = os.getcwd()

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
		cmdline(command1)
		cmdline(command2)
		cmdline(command3)

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

		command = 'echo "Rscript script_tximport.R one ' + self.experimentName + '" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o tximport.out -j y -N tximport -hold_jid salmon'
		cmdline(command)

	def countSRA(self):
		file  = open(self.shScript, 'r').read()
		count = file.count('echo')
		return count / 2

	def setNumberSRAinScripts(self):
		num     = self.countSRA()
		command = "sed -i -e 's/{sra number}/1-" + str(int(num)) + "/' "
		cmdline(command + self.bbdukScript)
		cmdline(command + self.salmonScript)
		
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

def executeDLRNAseq(gse, experimentsFolder):
	data = writeNewShFile(gse)
	ex = wrapp(data['experimentName'], data['script'], experimentsFolder)
	ex.createNewFolder()
	ex.copyScripta()
	ex.changeToExpFolder()
	ex.setNumberSRAinScripts()
	ex.executeSH()
	ex.executeBBduk()
	ex.executeSalmon()
	ex.backToScripts()
	ex.executeTximport()

def writeNewShFile(gse):
	SRAs         = []
	GSE          = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gds&term==' + gse + '&usehistory=y')
	GSEweb       = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gds&query_key=1&WebEnv=' + GSE['eSearchResult']['WebEnv'])
	PubMedIds    = serachItem(GSEweb['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'PubMedIds')
	PubMedIdsTag = serachItem(PubMedIds, 'Item', 'Name', 'int')
	PubMedId     = getValue(PubMedIdsTag)
	
	Samples      = serachItem(GSEweb['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'Samples')
	Accession    = serachItem(Samples['Item'][0], 'Item', 'Name', 'Accession')
	gsm          = getValue(Accession)

	for sample in Samples['Item']:
		Accession = serachItem(sample, 'Item', 'Name', 'Accession')
		Title     = serachItem(sample, 'Item', 'Name', 'Title')
		sra       = {
			'Accession': getValue(Accession),
			'Title': getValue(Title),
			'srx': ''
		}
		SRAs.append(sra)

	for DocSum in GSEweb['eSummaryResult']['DocSum']:
		Accession = serachItem(DocSum, 'Item', 'Name', 'Accession')
		for sra in SRAs:
			if getValue(Accession) == sra['Accession']:
				ExtRelations = serachItem(DocSum, 'Item', 'Name', 'ExtRelations')
				ExtRelation  = serachItem(ExtRelations, 'Item', 'Name', 'ExtRelation')
				TargetObject = serachItem(ExtRelation, 'Item', 'Name', 'TargetObject')
				sra['srx']   = getValue(TargetObject)
				break

	GSM            = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gds&term==' + gsm + '&usehistory=y')
	GSMweb         = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=gds&query_key=1&WebEnv=' + GSM['eSearchResult']['WebEnv'])
	ExtRelations   = serachItem(GSMweb['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'ExtRelations')
	TargetObject   = serachItem(ExtRelations['Item'], 'Item', 'Name', 'TargetObject')
	srp            = getValue(TargetObject)
	
	PUBMED         = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=' + PubMedId + '&version=2.0')
	experimentName = setExperimentName(PUBMED['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['SortFirstAuthor'], PUBMED['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['PubDate'])
	
	SRP            = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=sra&term=' + srp + '&usehistory=y')
	SRPweb         = getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=sra&query_key=1&WebEnv=' + SRP['eSearchResult']['WebEnv'])
	scriptDoc      = os.getcwd() + '/script_' + experimentName + '.sh'
	for DocSum in SRPweb['eSummaryResult']['DocSum']:
		ExpXml  = serachItem(DocSum, 'Item', 'Name', 'ExpXml')
		XML     = getValue(ExpXml)
		data    = xmltodict.parse('<root>' + XML + '</root>')
		Runs    = serachItem(DocSum, 'Item', 'Name', 'Runs')
		RunsTag = getValue(Runs)
		RunsXML = xmltodict.parse('<root>' + RunsTag + '</root>')
		if str(type(RunsXML['root']['Run'])) == "<class 'list'>":
			SRA = RunsXML['root']['Run'][0]['@acc']
		else:
			SRA = RunsXML['root']['Run']['@acc']
		urlDownload   = 'ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/' + SRA[0:3] + '/' + SRA[0:6] + '/' + SRA + '/' + SRA + '.sra'
		gsmAccesssion = ''
		srxAccession  = ''

		if existsKey(data['root'], 'Summary'):
			if existsKey(data['root']['Summary'], 'Title'):
				gsmAccesssion = getGSMFromTitle(data['root']['Summary']['Title'])

		if gsmAccesssion == '':
			if existsKey(data['root'], 'Experiment'):
				if existsKey(data['root']['Experiment'], '@name'):
					gsmAccesssion = getGSMFromTitle(data['root']['Experiment']['@name'])

		if existsKey(data['root'], 'Experiment'):
			if existsKey(data['root']['Experiment'], '@acc'):
				srxAccession = getGSMFromTitle(data['root']['Experiment']['@acc'])
		condition = serachCondition(SRAs, gsmAccesssion, 'Accession')
		if condition == '':
			condition = serachCondition(SRAs, srxAccession, 'srx')

		if condition == '':
			print('error: ' + SRA)
		else:
			condition = SRA + '_' + condition.replace(' ', '_')
			newlines  = 'echo "wget -nv ' + urlDownload + ' -O ' + condition + '.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o ' + condition + '_download.out -j y -N download_' + condition + "\n"
			newlines  = newlines + 'echo "fastq-dump --gzip --split-files ./' + condition + '.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o ' + condition + '_fqdump.out -j y -N dump_' + condition + ' -hold_jid download_' + condition + "\n\n"
			wirteSHDoc(scriptDoc, newlines)

	return {
		'experimentName': experimentName,
		'script': scriptDoc
	}

def wirteSHDoc(experimentName, lines):
	with open(experimentName, "a") as file:
	  	file.write(lines)

def existsKey(data, key):
	if key in data:
		return True
	return False

def serachCondition(SRAS, value, cond):
	for sra in SRAS:
		if sra[cond] == value:
			return sra['Title']
	return ''

def getGSMFromTitle(title):
	return title[0:10]

def setExperimentName(author, date):
	return author.split(' ')[0] + '_' + date.split(' ')[0]

def getValue(data):
	return data['#text']

def serachItem(data, tag, property, value):
	property = '@' + property
	if tag in data:
		if str(type(data[tag])) == "<class 'list'>":
			for item in data[tag]:
				if property in item:
					if item[property] == value:
						return item
		else:
			if property in data[tag]:
				if data[tag][property] == value:
					return data[tag]


def getXML(url):
	print('executing... ' + url)
	file = urllib2.urlopen(url)
	data = file.read()
	file.close()
	data = xmltodict.parse(data)
	return data 


def getArguments():
	"""Get the arguments of the command executed
	
	Get the arguments and their values of the program execution
	
	Returns:
		Object -- The arguments with values
	"""
	parser = argparse.ArgumentParser()
	parser.add_argument("-g", "--gse", help = "The GSE accession")
	parser.add_argument("-f", "--experiments-folder", help = "The folder to set all the information")

	data   = parser.parse_args()
	if isEmpty(data.gse):
		print("Is required the GSE accession")
		parser.print_help()
		exit()
	if isEmpty(data.experiments_folder):
		data.experiments_folder = '/home/rdorado/Chlamy/'
	if not data.experiments_folder.endswith('/'):
		data.experiments_folder = data.experiments_folder + '/'
	return data

arguments = getArguments()
executeDLRNAseq(arguments.gse, arguments.experiments_folder)

#python3 DLRNAseq.py -g GSE101944