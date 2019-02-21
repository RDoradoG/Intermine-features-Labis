#!/bin/bash

#$ -S /bin/bash
#$ -q all.q
#$ -cwd
#$ -pe threads 3
#$ -N bbduk
#$ -t {sra number}
#$ -hold_jid dump_SRR*

OUTDIR=Clean
mkdir -p $OUTDIR

infile_R1=`ls -1 *_1.fastq.gz | head -n $SGE_TASK_ID | tail -n 1`
infile_R2=${infile_R1/_1.fastq.gz/_2.fastq.gz}
BASE=${infile_R1/_1.fastq.gz}
outfile_R1="$OUTDIR"/"$BASE"_clean_R1.fastq.gz
outfile_R2="$OUTDIR"/"$BASE"_clean_R2.fastq.gz
stat_out="$OUTDIR"/"$BASE"_clean_stats.txt]
stat_perrefout="$OUTDIR"/"$BASE"_clean_perrefstats.txt
echo Processing $BASE

if [ -f $infile_R2 ]
then
  OPTIONS="in=$infile_R1 in2=$infile_R2 out=$outfile_R1 out2=$outfile_R2"
else
  OPTIONS="in=$infile_R1 out=$outfile_R1"
fi

bbduk2.sh -Xmx3g rref=/usr/local/Bioinf/Trimmomatic-0.36/adapters/TruSeq2-PE.fa,/usr/local/Bioinf/Trimmomatic-0.36/adapters/TruSeq2-SE.fa,/usr/local/Bioinf/Trimmomatic-0.36/adapters/TruSeq3-PE-2.fa,/usr/local/Bioinf/Trimmomatic-0.36/adapters/TruSeq3-PE.fa,/usr/local/Bioinf/Trimmomatic-0.36/adapters/TruSeq3-SE.fa fref=/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/rfam-5.8s-database-id98.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/rfam-5s-database-id98.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-arc-16s-id95.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-arc-23s-id98.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-bac-16s-id90.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-bac-23s-id98.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-euk-18s-id95.fasta,/usr/local/Bioinf/sortmerna-2.1b/rRNA_databases/silva-euk-28s-id98.fasta $OPTIONS stats=$stat_out showspeed=t k=27 rcomp=t maskmiddle=t threads=$NSLOTS qtrim=w trimq=20 mlf=0.6 qin=33 overwrite=true

