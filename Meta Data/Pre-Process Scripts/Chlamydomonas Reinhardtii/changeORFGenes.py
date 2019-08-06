from xml.dom import minidom

file_xml   = raw_input()
file_table = raw_input()
new_file   = raw_input()
key_table  = list(())

with open(file_table) as f:
	for line in f:
		columns        = line.split("	")
		all_first_data = columns[0].split("|")
		key_table.append({
			"accession": all_first_data[1],
			"name":  all_first_data[2],
			"newKey": columns[1]
			})

mydoc       = minidom.parse(file_xml)
all_uniprot = mydoc.getElementsByTagName('uniprot')
uniprot     = all_uniprot[0]
entrys      = uniprot.getElementsByTagName('entry')
for entry in entrys:
	accessions = entry.getElementsByTagName('accession')
	genes      = entry.getElementsByTagName('gene')
	kewnames   = list(())
	for accession in accessions:
		for row in key_table:
			if row["accession"] == accession.firstChild.data:
				kewnames.append(row["newKey"])
				#key_table.remove(row)

	for gene in genes:
		geneNames = gene.getElementsByTagName('name')
		ORFS      = list(())
		for genaName in geneNames:
			if genaName.attributes['type'].value == "ORF":
				gene.removeChild(genaName)

		for keyname in kewnames:
			newORF     = mydoc.createElement("name")
			newORF.setAttribute("type", "ORF")
			label      = keyname.replace("\r\n", "")
			newORFText = mydoc.createTextNode(label)
			newORF.appendChild(newORFText)
			gene.appendChild(newORF)
    
file_handle = open(new_file, "wb")
mydoc.writexml(file_handle)
file_handle.close()
