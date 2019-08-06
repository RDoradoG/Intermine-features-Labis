import sys, getopt, subprocess, os, argparse
from gene import Gene
from bioEntity import bioEntity

class Gff3(object):
	"""docstring for Gff3"""
	def __init__(self, filename):
		self.filename          = filename
		self.genes             = {}
		self.transcripts       = {}
		self.inronsGemnome     = {}
		self.sequencesFeatures = {}
		self.introns           = {}
		self.entitiesAlone     = []
		self.cdsAlone          = []
		self.max_position      = 0
		self.execute_geneMap   = False

	def set_gffreadPath(self, gffreadPath):
		self.gffreadPath = gffreadPath

	def set_gffFile(self, fileName):
		self.gffFile_name = fileName
		self.gffFile      = open(fileName,'w')

	def close_gffFile(self):
		self.gffFile.close()

	def close_geneMapFile(self):
		self.genemapFile.close()

	def readFile(self):
		with open(self.filename) as allFile:
			for line in allFile:
				line     = line.strip()
				cols     = line.split("\t")
				entity   = cols[2]
				extras   = []
				lastCol  = cols[8].split(";")
				tmp_end  = int(cols[4])
				ID       = ''
				Name     = ''
				Parent   = ''
				GeneAtrr = ''
				for info in lastCol:
					data = info.split("=")
					if data[0] == 'ID':
						ID = data[1]
						continue

					if data[0] == 'Name':
						Name = data[1]
						continue

					if data[0] == 'Parent':
						Parent = data[1]
						continue

					if data[0] == 'gene':
						GeneAtrr = data[1]
						continue

					extras.append(info)

				extras_str = ";".join(extras)

				if tmp_end > self.max_position:
					self.max_position = tmp_end + 1

				bio_entity = bioEntity()
				bio_entity.set_Chromosome(cols[0])
				bio_entity.set_Source(cols[1])
				bio_entity.set_Type(cols[2])
				bio_entity.set_Init(int(cols[3]))
				bio_entity.set_End(tmp_end)
				bio_entity.set_Score(cols[5])
				bio_entity.set_Strand(cols[6])
				bio_entity.set_ScoreCDS(cols[7])
				bio_entity.set_ID(ID)
				bio_entity.set_Name(Name)
				bio_entity.set_Parent(Parent)
				bio_entity.set_Extras(extras_str)
				bio_entity.set_Gene(GeneAtrr)

				if entity == 'gene':
					self.setNewGene(ID, bio_entity)


				if entity == 'mRNA' or entity == 'tRNA' or entity == 'rRNA':
					if Parent not in self.genes:
						self.setNewGene(Parent, bioEntity())

					self.genes[Parent].add_transcript(bio_entity)
					self.transcripts[ID] = Parent

				if entity == 'five_prime_UTR':
					self.genes[self.transcripts[Parent]].add_fivePrimeUTR(bio_entity)

				if entity == 'three_prime_UTR':
					self.genes[self.transcripts[Parent]].add_threePrimeUTR(bio_entity)


				if entity == 'sequence_feature':
					self.sequencesFeatures[ID] = bio_entity

				if entity == 'intron':
					if GeneAtrr != '':
						if GeneAtrr not in self.genes:
							GeneAtrr = 'gene-' + GeneAtrr + '-2'
							bio_entity.set_Gene(GeneAtrr)
							if GeneAtrr not in self.genes:
								self.setNewGene(GeneAtrr, bioEntity())
							
						self.genes[GeneAtrr].add_introns(bio_entity)
						self.introns[ID] = GeneAtrr

					else:
						self.inronsGemnome[ID] = bio_entity


				if entity == 'CDS':
					if Parent in self.genes:
						self.genes[Parent].add_cds(bio_entity)

					else:
						if Parent in self.transcripts:
							self.genes[self.transcripts[Parent]].add_cds(bio_entity)

						else:
							self.cdsAlone.append(bio_entity)

				if entity == 'exon':
					if Parent in self.genes:
						self.genes[Parent].add_exons(bio_entity)

					else:
						if Parent in self.transcripts:
							self.genes[self.transcripts[Parent]].add_exons(bio_entity)

						else:
							self.entitiesAlone.append(bio_entity)

	def setNewGene(self, ID, bio_entity):
		if ID not in self.genes:
			self.genes[ID] = Gene()

		if bio_entity.get_ID() != '':
			self.genes[ID].add_bioEntity(bio_entity)

	def addGenesToEmptyCDS(self):
		for cds in self.cdsAlone:
			newId      = cds.get_ID().replace("cds", "gene")
			bio_entity = bioEntity()
			bio_entity.set_Chromosome(cds.get_Chromosome())
			bio_entity.set_Source(cds.get_Source())
			bio_entity.set_Type('gene')
			bio_entity.set_Init(cds.get_Init())
			bio_entity.set_End(cds.get_End())
			bio_entity.set_Score('.')
			bio_entity.set_Strand(cds.get_Strand())
			bio_entity.set_ScoreCDS('.')
			bio_entity.set_ID(newId)
			bio_entity.set_Name(newId)
			cds.set_Parent(newId)
			self.setNewGene(newId, bio_entity)
			self.genes[newId].add_cds(cds)


	def addTranscriptsToEmptyGenes(self):
		for key in self.genes:
			gene = self.genes[key]
			if gene.size_transcript() == 0:
				if gene.size_cds() == 0:
					print("ATTENTION!!! Este cuate no se puede deducir: " + key)
				
				else:

					init         = self.max_position
					end          = 0
					new_id       = key + '-mRNA-1'
					new_entities = []
					if gene.size_exons() > 0:
						for exon in gene.get_exons():
							init = self.checkPosition(exon.get_Init(), init, True)
							end = self.checkPosition(exon.get_End(), end, False)
							exon.set_Parent(new_id)
							new_entities.append(exon)
						
						self.genes[key].change_exons(new_entities)

					else:
						for cds in gene.get_cds():
							init = self.checkPosition(cds.get_Init(), init, True)
							end = self.checkPosition(cds.get_End(), end, False)
							cds.set_Parent(new_id)
							new_entities.append(cds)

						self.genes[key].change_cds(new_entities)					
						
					transcript = self.createTranscript(gene.get_bioEntity(), 0, init, end, new_id, key)
					self.genes[key].add_transcript(transcript)



	def checkPosition(self, x, y, direction):
		if direction:
			decision = x < y

		else:
			decision = x > y

		if decision:
			return x

		return y
		

	def createTranscript(self, gene, pos, init, end, new_id, parent):
		bio_entity = bioEntity()
		bio_entity.set_Chromosome(gene[pos].get_Chromosome())
		bio_entity.set_Source(gene[pos].get_Source())
		bio_entity.set_Type("mRNA")
		bio_entity.set_Init(init)
		bio_entity.set_End(end)
		bio_entity.set_Strand(gene[pos].get_Strand())
		bio_entity.set_ID(new_id)
		bio_entity.set_Name(new_id)
		bio_entity.set_Parent(parent)
		return bio_entity

	def printGene(self, ID):
		for gene in self.genes[ID].get_bioEntity():
			self.print_gff(gene.get_line())

		for transcript in self.genes[ID].get_transcript():
			self.print_gff(transcript.get_line())

		for cds in self.genes[ID].get_cds():
			self.print_gff(cds.get_line())

		for exon in self.genes[ID].get_exons():
			self.print_gff(exon.get_line())

		for intron in self.genes[ID].get_introns():
			self.print_gff(intron.get_line())

		for fivePrimeUTR in self.genes[ID].get_fivePrimeUTR():
			self.print_gff(fivePrimeUTR.get_line())

		for threePrimeUTR in self.genes[ID].get_threePrimeUTR():
			self.print_gff(threePrimeUTR.get_line())

	def print_sequencesFeatures(self):
		for key in self.sequencesFeatures:
			self.print_gff(self.sequencesFeatures[key].get_line())

	def print_inronsGemnome(self):
		for key in self.inronsGemnome:
			self.print_gff(self.inronsGemnome[key].get_line())

	def print_entitiesAlone(self):
		for entitieAlone in self.entitiesAlone:
			self.print_gff(entitieAlone.get_line())

	def print_genes(self):
		for key in self.genes:
			self.printGene(key)

	def print_gff(self, text):
		self.gffFile.write(text + "\n")

	def print_genemap(self, transcript, gene):
		self.genemapFile.write(transcript + "\t" + gene + "\n")

	def execute_gffRead(self, fasta):
		cmdline(self.gffreadPath + " " + self.gffFile_name + " -g " + fasta + " -w " + fasta + ".new")

	def setGeneMap(self, genemapFile):
		if genemapFile != '':
			self.execute_geneMap = True
			self.genemapFile     = open(genemapFile,'w')

	def executeGeneMap(self):
		for key in self.genes:
			for transcript in self.genes[key].get_transcript():
				self.print_genemap(transcript.get_ID(), key)


def getArguments():
	"""Get the arguments of the command executed
	
	Get the arguments and their values of the program execution
	
	Returns:
		Object -- The arguments with values
	"""
	parser = argparse.ArgumentParser()
	parser.add_argument("-g", "--gff3", help = "GFF3 file")
	parser.add_argument("-f", "--fasta", help = "Genome fasta file")
	parser.add_argument("-r", "--gffread", help = "Path to gffRead bin")
	parser.add_argument("-m", "--geneMap", help = "geneMap file")

	data   = parser.parse_args()

	if isEmpty(data.gff3):
		print("Is required the GFF3 file.")
		parser.print_help()
		exit()

	#if isEmpty(data.fasta):
		#print("Is required the fasta file.")
		#parser.print_help()
		#exit()

	if isEmpty(data.geneMap):
		data.geneMap = ''

	if isEmpty(data.gffread):
		data.gffread = '/home/rdorado/gffread/gffread/gffread'

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
gff3 = Gff3(arguments.gff3)
gff3.readFile()
gff3.addGenesToEmptyCDS()
gff3.addTranscriptsToEmptyGenes()
gff3.set_gffFile(arguments.gff3 + ".new")
gff3.print_genes()

#gff3.print_sequencesFeatures()
#gff3.print_inronsGemnome()
#gff3.print_entitiesAlone()

gff3.close_gffFile()

gff3.set_gffreadPath(arguments.gffread)
gff3.execute_gffRead(arguments.fasta)

gff3.setGeneMap(arguments.geneMap)
gff3.executeGeneMap()