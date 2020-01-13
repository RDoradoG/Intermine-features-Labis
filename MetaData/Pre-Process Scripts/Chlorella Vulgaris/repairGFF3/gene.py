from bioEntity import bioEntity

class Gene():
	"""docstring for Gene"""
	def __init__(self):
		self.transcript    = []
		self.cds           = []
		self.exons         = []
		self.introns       = []
		self.fivePrimeUTR  = []
		self.threePrimeUTR = []
		self.bio_entity    = []
		
	def get_transcript(self):
		return self.transcript

	def size_transcript(self):
		return len(self.transcript)

	def add_transcript(self, transcript):
		self.transcript.append(transcript)

	def get_cds(self):
		return self.cds

	def add_cds(self, cds):
		self.cds.append(cds)

	def get_introns(self):
		return self.introns

	def add_introns(self, introns):
		self.introns.append(introns)

	def size_cds(self):
		return len(self.cds)

	def add_fivePrimeUTR(self, fivePrimeUTR):
		self.fivePrimeUTR.append(fivePrimeUTR)

	def get_fivePrimeUTR(self):
		return self.fivePrimeUTR

	def add_threePrimeUTR(self, threePrimeUTR):
		self.threePrimeUTR.append(threePrimeUTR)

	def get_threePrimeUTR(self):
		return self.threePrimeUTR

	def change_cds(self, cds):
		self.cds = cds

	def size_exons(self):
		return len(self.exons)

	def get_exons(self):
		return self.exons

	def add_exons(self, exons):
		self.exons.append(exons)

	def change_exons(self, exons):
		self.exons = exons

	def get_bioEntity(self):
		return self.bio_entity

	def add_bioEntity(self, bio_entity):
		self.bio_entity.append(bio_entity)
