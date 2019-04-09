import sys, getopt, os, argparse, re, subprocess, time, datetime

from uniprotXML import uniprotXML
from goAnnotation import goAnnotation
from annotation_file import annotation_file
from kegg import kegg
from eggNOG import eggNOG
from PlnTFDB import PlnTFDB
from gff import gff
from fasta import fasta

from library import *

def getArguments(folder):
    """Get the arguments of the command executed

    Get the arguments and their values of the program execution

    Returns:
        Object -- The arguments with values
    """
    #python getUniprotXML.py --fasta_proteins
    parser = argparse.ArgumentParser()

    parser.add_argument("--uniprot", action = 'store_true', help = "Run the script to get the xml of uniprot")
    parser.add_argument("--go_annotation", action = 'store_true', help = "Run the script to get the go information")
    parser.add_argument("--kegg", action = 'store_true', help = "Run the script to get the kegg information")
    parser.add_argument("--eggNOG", action = 'store_true', help = "Run the script to get the eggNOG information")
    parser.add_argument("--PlnTFDB", action = 'store_true', help = "Run the script to get the PlnTFDB information")
    parser.add_argument("--Annotation_info", action = 'store_true', help = "Run the script to get the Annotation information new files")

    parser.add_argument("--folder_name", help = "The name of the folder qith the new files")

    parser.add_argument("--fasta_proteins", help = "The fasta file of the proteins")
    parser.add_argument("--fasta_uniprot", help = "The fasta file of the proteins of uniprot")
    parser.add_argument("--xml_uniprot", help = "The xml file of the proteins of uniprot")
    parser.add_argument("--step_uniprot", help = "The fasta file of the proteins of uniprot")

    parser.add_argument("--annotation_file", help = "The file wioth all the annotation information.")
    parser.add_argument("--obo_file", help = "The obo file.")
    parser.add_argument("--gff_file", help = "The GFF3 file of the genomic information.")
    parser.add_argument("--taxon", help = "The taxon number of the organism.")
    #Chlamydomonas Reinhardtii --> 3055
    #Chlorella Vulgaris --> 3077

    parser.add_argument("--cpu", help = "The cpu number to use in hmmsearch.")
    parser.add_argument("--hmm_file", help = "The hmm file to use.")
    parser.add_argument("--rules_file", help = "The file with the rules to get PlnTFDB families.")

    data   = parser.parse_args()
    to_exit = False

    if data.uniprot:
        if isEmpty(data.fasta_proteins):
            print("Is required the fasta file of proteins")
            to_exit = True

        if isEmpty(data.fasta_uniprot):
            print("Is required the fasta file of proteins of uniprot")
            to_exit = True

        if isEmpty(data.xml_uniprot):
            print("Is required the xml file of proteins of uniprot")
            to_exit = True

        if isEmpty(data.taxon):
            print("Is required the taxon number")
            to_exit = True

        if isEmpty(data.step_uniprot):
            data.step_uniprot = '1'

        if isEmpty(data.gff_file):
            print("Is required the GFF3 file of the genomic information")
            to_exit = True

    if data.go_annotation:
        if isEmpty(data.annotation_file):
            print("Is required the file of annotations for execute go annotation")
            to_exit = True

        if isEmpty(data.obo_file):
            print("Is required the .obo file")
            to_exit = True

        if isEmpty(data.taxon):
            print("Is required the taxon number")
            to_exit = True

        if isEmpty(data.gff_file):
            print("Is required the GFF3 file of the genomic information")
            to_exit = True

    if data.kegg:
        if isEmpty(data.annotation_file):
            print("Is required the file of annotations for execute kegg")
            to_exit = True

        if isEmpty(data.gff_file):
            print("Is required the GFF3 file of the genomic information")
            to_exit = True

    if data.eggNOG:
        if isEmpty(data.annotation_file):
            print("Is required the file of annotations for execute kegg")
            to_exit = True

        if isEmpty(data.gff_file):
            print("Is required the GFF3 file of the genomic information")
            to_exit = True

    if data.PlnTFDB:
        if isEmpty(data.fasta_proteins):
            print("Is required the fasta file of proteins for PlnTFDB")
            to_exit = True

        if isEmpty(data.cpu):
            data.cpu = '1'

        if isEmpty(data.hmm_file):
            data.hmm_file = folder + 'OwnPlnTFDB.hmm'

        if isEmpty(data.rules_file):
            data.rules_file = folder + 'RulesFull'

    if data.Annotation_info:
        if isEmpty(data.fasta_proteins):
            print("Is required the fasta file of proteins")
            to_exit = True
            
        if isEmpty(data.annotation_file):
            print("Is required the file of annotations for execute kegg")
            to_exit = True

        if isEmpty(data.gff_file):
            print("Is required the GFF3 file of the genomic information")
            to_exit = True

    if isEmpty(data.folder_name):
        data.folder_name = 'phycomine'

    if to_exit:
        parser.print_help()
        exit()

    return data

now             = datetime.datetime.now()
folder          = getFolderofFile(sys.argv[0])

arguments       = getArguments(folder)

createFolder(arguments.folder_name)

Gff_file        = gff(arguments)
Gff_file.getobjectResult()

Fasta = fasta(arguments, Gff_file)
Fasta.getobjectResult()

Annotation_file = annotation_file(arguments, Gff_file)
annotations     = Annotation_file.getobjectResult()
Annotation_file.execute()

GoAnnotation    = goAnnotation(arguments, now, annotations)
GoAnnotation.execute()

Kegg            = kegg(arguments, annotations)
Kegg.execute()

EggNOG          = eggNOG(arguments, annotations)
EggNOG.execute()

plnTFDB         = PlnTFDB(arguments, folder)
plnTFDB.execute()

argumentline    = 'python3 ' + sys.argv[0]
UniprotXML      = uniprotXML(arguments, argumentline, Gff_file)
UniprotXML.execute()