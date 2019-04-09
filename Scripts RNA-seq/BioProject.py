import xmltodict, os
import urllib.request as urllib2

class BioProject():
	"""docstring for BioProject"""
	def __init__(self, gse, srp, pubmedid):
		self.gse                  = gse
		self.srp                  = srp
		self.alternative_pubmedid = []
		if not self.isEmpty(pubmedid):
			self.alternative_pubmedid = pubmedid.split(',')

	def verifyData(self):
		if not self.isEmpty(self.gse):
			obj = self.getInfo(self.gse)

		else:
			obj = self.getGSE(self.srp)

		self.SRAs           = obj['sras']
		self.Project        = obj['Project']
		self.experimentName = self.getUniqueExperimentName(obj['experimentNames'])
		self.PubMedId       = self.getUniquePubMedId(obj['PubMedIds'])

	def getProjectInfo(self):
		return self.Project

	def getNumberOfSRAs(self):
		return len(self.SRAs)

	def getSRAsArray(self):
		return self.SRAs

	def getExperimentName(self):
		return self.experimentName

	def getPubMedId(self):
		return self.PubMedId

	def getScript(self):
		return self.script

	def getNumberOfSamples(self):
		return self.numberOfSamples

	def getUniquePubMedId(self, PubMedIds):
		return ','.join(PubMedIds)

	def getUniqueExperimentName(self, experimentNames):
		if len(experimentNames) > 0:
			return '_'.join(experimentNames)

		else:
			if len(self.alternative_pubmedid):
				names = []
				for alt_pubmedid in self.alternative_pubmedid:
					names.append(self.getExperimntsNames(alt_pubmedid))

				return '_'.join(names)

			else:
				return self.Project['Project_Acc']

	def getInfo(self, gse):
		Project       = self.getBioProjectInfo(gse)
		GSEweb        = self.getXMLOfUrl(gse + '[GEO%20Accession]', 'gds')
		PubMedIds     = self.serachItem(GSEweb['data']['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'PubMedIds')
		PubMedIdsTags = self.serachItems(PubMedIds, 'Item', 'Name', 'int')
		PubMedIds     = []
		for PubMedIdsTag in PubMedIdsTags:
			PubMedIds.append(self.getValue(PubMedIdsTag))	

		Samples         = self.serachItem(GSEweb['data']['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'Samples')
		Accession       = self.serachItem(Samples['Item'][0], 'Item', 'Name', 'Accession')
		gsm             = self.getValue(Accession)
		SRAs            = self.getSRAs(Samples, GSEweb['data']['eSummaryResult']['DocSum'])
		experimentNames = []
		for PubMedId in PubMedIds:
			experimentNames.append(self.getExperimntsNames(PubMedId))
		
		self.srp = self.getSRP(gsm)
		
		return {
			'sras': SRAs,
			'experimentNames': experimentNames,
			'PubMedIds': PubMedIds,
			'Project': Project
		}

	def getSRAOfSRP(self, DocSum):
		ExpXml = self.serachItem(DocSum, 'Item', 'Name', 'ExpXml')
		XML     = self.getValue(ExpXml)
		data    = xmltodict.parse('<root>' + XML + '</root>')
		return {
			'Accession': '',
			'Title': data['root']['Summary']['Title'],
			'srx': data['root']['Experiment']['@acc'],
			'library': data['root']['Library_descriptor']['LIBRARY_STRATEGY']
		}

	def getGSE(self, srp):
		SRPweb = self.getXMLOfUrl(srp, 'sra')
		if int(SRPweb['count']) > 1:
			ExpXml = self.serachItem(SRPweb['data']['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'ExpXml')

		else:
			ExpXml = self.serachItem(SRPweb['data']['eSummaryResult']['DocSum'], 'Item', 'Name', 'ExpXml')

		XML    = self.getValue(ExpXml)
		data   = xmltodict.parse('<root>' + XML + '</root>')
		if self.existsKey(data['root'], 'Bioproject'):
			Bioproject = data['root']['Bioproject']

		##if self.isEmpty(Bioproject):
			##return '' ##falta aqui

		PRJNWeb = self.getXMLOfUrl(Bioproject, 'gds')
		gse = ''

		if PRJNWeb['status'] == 'ok':
			if self.existsKey(PRJNWeb['data']['eSummaryResult']['DocSum'], 'Accession'):
				Accession = self.serachItem(PRJNWeb['data']['eSummaryResult']['DocSum'], 'Item', 'Name', 'Accession')
				gse = self.getValue(Accession)

		if not self.isEmpty(gse):
			return self.getInfo(gse)

		Project = self.getBioProjectInfo(Bioproject)

		SRAs = []

		if int(SRPweb['count']) > 1:
			for DocSum in SRPweb['data']['eSummaryResult']['DocSum']:
				SRAs.append(self.getSRAOfSRP(DocSum))

		else:
			SRAs.append(self.getSRAOfSRP(SRPweb['data']['eSummaryResult']['DocSum']))
	
		return {
			'sras': SRAs,
			'experimentNames': [],
			'PubMedIds': [],
			'Project': Project
		}

	def writeNewShFile(self):
		SRPwebCount          = self.getXMLOfUrl(self.srp, 'sra')
		self.script          = os.getcwd() + '/script_' + self.experimentName + '.sh'
		self.numberOfSamples = SRPwebCount['count']
		SRPweb               = SRPwebCount['data']
		if int(self.numberOfSamples) > 1:
			for DocSum in SRPweb['eSummaryResult']['DocSum']:
				self.runARun(DocSum)

		else:
			self.runARun(SRPweb['eSummaryResult']['DocSum'])

	def getXMLOfUrl(self, value, dba):
		data  = self.getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=' + dba + '&term=' + value + '&usehistory=y')
		if self.existsKey(data['eSearchResult'], 'WarningList'):
			return {
				'data': {},
				'count': 0,
				'status': 'error'
			} 

		count = data['eSearchResult']['TranslationStack']['TermSet']['Count']
		info = self.getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=' + dba + '&query_key=1&WebEnv=' + data['eSearchResult']['WebEnv'])
		return {
			'data': info,
			'count': count,
			'status': 'ok'
		}

	def getBioProjectInfo(self, gse):
		GSEweb        = self.getXMLOfUrl(gse + '[Project%20Accession]', 'bioproject')
		return {
			'Project_Acc': GSEweb['data']['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['Project_Acc'],
			#'Project_Name': GSEweb['data']['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['Project_Name'],
			'Project_Title': GSEweb['data']['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['Project_Title'],
			'Project_Description': GSEweb['data']['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['Project_Description']
		}

	def serachItem(self,data, tag, property, value):
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

	def serachItems(self,data, tag, property, value):
		property = '@' + property
		items = []
		if tag in data:
			if str(type(data[tag])) == "<class 'list'>":
				for item in data[tag]:
					if property in item:
						if item[property] == value:
							items.append(item)

			else:
				if property in data[tag]:
					if data[tag][property] == value:
						items.append(data[tag])

		return items

	def getSRAs(self, Samples, GSEweb):
		SRAs = []
		for sample in Samples['Item']:
			Accession = self.serachItem(sample, 'Item', 'Name', 'Accession')
			Accession = self.getValue(Accession)
			library   = self.getLibraryOfSRA(Accession)
			Title     = self.serachItem(sample, 'Item', 'Name', 'Title')
			sra       = {
				'Accession': Accession,
				'Title': self.getValue(Title),
				'srx': '',
				'library': library
			}
			SRAs.append(sra)

		if not self.isEmpty(GSEweb):
			for DocSum in GSEweb:
				Accession = self.serachItem(DocSum, 'Item', 'Name', 'Accession')
				for sra in SRAs:
					if self.getValue(Accession) == sra['Accession']:
						ExtRelations = self.serachItem(DocSum, 'Item', 'Name', 'ExtRelations')
						ExtRelation  = self.serachItem(ExtRelations, 'Item', 'Name', 'ExtRelation')
						TargetObject = self.serachItem(ExtRelation, 'Item', 'Name', 'TargetObject')
						sra['srx']   = self.getValue(TargetObject)
						break

		return SRAs

	def runARun(self, DocSum):
		fastq_dump_command = '/usr/local/Bioinf/sratoolkit.2.9.0-ubuntu64/bin/fastq-dump'
		ExpXml  = self.serachItem(DocSum, 'Item', 'Name', 'ExpXml')
		XML     = self.getValue(ExpXml)
		data    = xmltodict.parse('<root>' + XML + '</root>')
		Runs    = self.serachItem(DocSum, 'Item', 'Name', 'Runs')
		RunsTag = self.getValue(Runs)
		RunsXML = xmltodict.parse('<root>' + RunsTag + '</root>')
		if str(type(RunsXML['root']['Run'])) == "<class 'list'>":
			SRA = RunsXML['root']['Run'][0]['@acc']

		else:
			SRA = RunsXML['root']['Run']['@acc']
		
		urlDownload  = 'ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/' + SRA[0:3] + '/' + SRA[0:6] + '/' + SRA + '/' + SRA + '.sra'
		gsmAccession = ''
		srxAccession = ''

		if self.existsKey(data['root'], 'Summary'):
			if self.existsKey(data['root']['Summary'], 'Title'):
				gsmAccession = self.getGSMFromTitle(data['root']['Summary']['Title'])

		if gsmAccession == '':
			if self.existsKey(data['root'], 'Experiment'):
				if self.existsKey(data['root']['Experiment'], '@name'):
					gsmAccession = self.getGSMFromTitle(data['root']['Experiment']['@name'])

		if self.existsKey(data['root'], 'Experiment'):
			if self.existsKey(data['root']['Experiment'], '@acc'):
				srxAccession = self.getGSMFromTitle(data['root']['Experiment']['@acc'])

		condition = self.serachCondition(self.SRAs, gsmAccession, 'Accession')

		if condition['status'] == 'error':
			condition = self.serachCondition(self.SRAs, srxAccession, 'srx')

		if condition['status'] == 'error':
			print('error: ' + SRA)

		if condition['status'] == 'Not RNA-seq':
			print('Not RNA-seq: ' + SRA)

		if condition['status'] == 'ok':
			condition_value = condition['value']
			condition_value = SRA + '_' + condition_value.replace(' ', '_').replace(':', '_').replace(',', '_').replace('=', '_')
			newlines        = 'echo "wget -nv ' + urlDownload + ' -O ' + condition_value + '.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o ' + condition_value + '_download.out -j y -N download_' + condition_value + "\n"
			newlines        = newlines + 'echo "' + fastq_dump_command + ' --gzip --split-files ./' + condition_value + '.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o ' + condition_value + '_fqdump.out -j y -N dump_' + condition_value + ' -hold_jid download_' + condition_value + "\n\n"
			self.wirteSHDoc(self.script, newlines)


	def getExperimntsNames(self, PubMedId):
		PUBMED = self. getXML('https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id=' + PubMedId + '&version=2.0')
		return self.setExperimentName(PUBMED['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['SortFirstAuthor'], PUBMED['eSummaryResult']['DocumentSummarySet']['DocumentSummary']['PubDate'])

	def getLibraryOfSRA(self, Accession):
		AccessionWeb = self.getXMLOfUrl(Accession, 'sra')
		ExpXml       = self.serachItem(AccessionWeb['data']['eSummaryResult']['DocSum'], 'Item', 'Name', 'ExpXml')
		XML          = self.getValue(ExpXml)
		data         = xmltodict.parse('<root>' + XML + '</root>')
		if self.existsKey(data['root'], 'Library_descriptor'):
				if self.existsKey(data['root']['Library_descriptor'], 'LIBRARY_STRATEGY'):
					return data['root']['Library_descriptor']['LIBRARY_STRATEGY']

		return ''

	def getValue(self,data):
		return data['#text']

	def setExperimentName(self,author, date):
		return author.split(' ')[0] + '_' + date.split(' ')[0]

	def getSRP(self, gsm):
		GSMweb       = self.getXMLOfUrl(gsm, 'gds')
		ExtRelations = self.serachItem(GSMweb['data']['eSummaryResult']['DocSum'][0], 'Item', 'Name', 'ExtRelations')
		TargetObject = self.serachItem(ExtRelations['Item'], 'Item', 'Name', 'TargetObject')
		return self.getValue(TargetObject)

	def serachCondition(self, SRAS, value, cond):
		for sra in SRAS:
			if sra[cond] == value:
				if sra['library'] == 'RNA-Seq':
					return {'status': 'ok', 'value': sra['Title']}
				else:
					return {'status': 'Not RNA-seq', 'value': ''}

		return {'status': 'error', 'value': ''}

	def existsKey(self,data, key):
		if key in data:
			return True
		return False

	def wirteSHDoc(self,experimentName, lines):
		with open(experimentName, "a") as file:
			file.write(lines)

	def getGSMFromTitle(self,title):
		return title[0:10]

	def getXML(self,url):
		print('executing... ' + url)
		file = urllib2.urlopen(url)
		data = file.read()
		file.close()
		data = xmltodict.parse(data)
		return data 

	def isEmpty(self, var):
		return var == None or var == '' or len(var) == 0