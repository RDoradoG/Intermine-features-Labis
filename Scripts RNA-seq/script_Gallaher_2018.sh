echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874702/SRR5874702.sra -O SRR5874702_post-germination.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874702_post-germination_download.out -j y -N download_SRR5874702_post-germination
echo "fastq-dump --gzip --split-files ./SRR5874702_post-germination.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874702_post-germination_fqdump.out -j y -N dump_SRR5874702_post-germination -hold_jid download_SRR5874702_post-germination

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874701/SRR5874701.sra -O SRR5874701_zygote.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874701_zygote_download.out -j y -N download_SRR5874701_zygote
echo "fastq-dump --gzip --split-files ./SRR5874701_zygote.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874701_zygote_fqdump.out -j y -N dump_SRR5874701_zygote -hold_jid download_SRR5874701_zygote

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874700/SRR5874700.sra -O SRR5874700_mt-_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874700_mt-_gamete_download.out -j y -N download_SRR5874700_mt-_gamete
echo "fastq-dump --gzip --split-files ./SRR5874700_mt-_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874700_mt-_gamete_fqdump.out -j y -N dump_SRR5874700_mt-_gamete -hold_jid download_SRR5874700_mt-_gamete

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874699/SRR5874699.sra -O SRR5874699_mt+_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874699_mt+_gamete_download.out -j y -N download_SRR5874699_mt+_gamete
echo "fastq-dump --gzip --split-files ./SRR5874699_mt+_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874699_mt+_gamete_fqdump.out -j y -N dump_SRR5874699_mt+_gamete -hold_jid download_SRR5874699_mt+_gamete

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874698/SRR5874698.sra -O SRR5874698_mt-_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874698_mt-_vegetative_download.out -j y -N download_SRR5874698_mt-_vegetative
echo "fastq-dump --gzip --split-files ./SRR5874698_mt-_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874698_mt-_vegetative_fqdump.out -j y -N dump_SRR5874698_mt-_vegetative -hold_jid download_SRR5874698_mt-_vegetative

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874697/SRR5874697.sra -O SRR5874697_mt+_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874697_mt+_vegetative_download.out -j y -N download_SRR5874697_mt+_vegetative
echo "fastq-dump --gzip --split-files ./SRR5874697_mt+_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874697_mt+_vegetative_fqdump.out -j y -N dump_SRR5874697_mt+_vegetative -hold_jid download_SRR5874697_mt+_vegetative

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874694/SRR5874694.sra -O SRR5874694_media_-Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874694_media_-Cu_download.out -j y -N download_SRR5874694_media_-Cu
echo "fastq-dump --gzip --split-files ./SRR5874694_media_-Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874694_media_-Cu_fqdump.out -j y -N dump_SRR5874694_media_-Cu -hold_jid download_SRR5874694_media_-Cu

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874691/SRR5874691.sra -O SRR5874691_media_+Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874691_media_+Cu_download.out -j y -N download_SRR5874691_media_+Cu
echo "fastq-dump --gzip --split-files ./SRR5874691_media_+Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874691_media_+Cu_fqdump.out -j y -N dump_SRR5874691_media_+Cu -hold_jid download_SRR5874691_media_+Cu

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874690/SRR5874690.sra -O SRR5874690_media_-Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874690_media_-Fe_-_poly-A_download.out -j y -N download_SRR5874690_media_-Fe_-_poly-A
echo "fastq-dump --gzip --split-files ./SRR5874690_media_-Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874690_media_-Fe_-_poly-A_fqdump.out -j y -N dump_SRR5874690_media_-Fe_-_poly-A -hold_jid download_SRR5874690_media_-Fe_-_poly-A

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874689/SRR5874689.sra -O SRR5874689_media_+Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874689_media_+Fe_-_poly-A_download.out -j y -N download_SRR5874689_media_+Fe_-_poly-A
echo "fastq-dump --gzip --split-files ./SRR5874689_media_+Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874689_media_+Fe_-_poly-A_fqdump.out -j y -N dump_SRR5874689_media_+Fe_-_poly-A -hold_jid download_SRR5874689_media_+Fe_-_poly-A

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874688/SRR5874688.sra -O SRR5874688_media_-Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874688_media_-Fe_-_RiboZero_download.out -j y -N download_SRR5874688_media_-Fe_-_RiboZero
echo "fastq-dump --gzip --split-files ./SRR5874688_media_-Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874688_media_-Fe_-_RiboZero_fqdump.out -j y -N dump_SRR5874688_media_-Fe_-_RiboZero -hold_jid download_SRR5874688_media_-Fe_-_RiboZero

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874687/SRR5874687.sra -O SRR5874687_media_+Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874687_media_+Fe_-_RiboZero_download.out -j y -N download_SRR5874687_media_+Fe_-_RiboZero
echo "fastq-dump --gzip --split-files ./SRR5874687_media_+Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874687_media_+Fe_-_RiboZero_fqdump.out -j y -N dump_SRR5874687_media_+Fe_-_RiboZero -hold_jid download_SRR5874687_media_+Fe_-_RiboZero

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874684/SRR5874684.sra -O SRR5874684_diurnal_growth_-_light.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874684_diurnal_growth_-_light_download.out -j y -N download_SRR5874684_diurnal_growth_-_light
echo "fastq-dump --gzip --split-files ./SRR5874684_diurnal_growth_-_light.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874684_diurnal_growth_-_light_fqdump.out -j y -N dump_SRR5874684_diurnal_growth_-_light -hold_jid download_SRR5874684_diurnal_growth_-_light

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874681/SRR5874681.sra -O SRR5874681_diurnal_growth_-_dark.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874681_diurnal_growth_-_dark_download.out -j y -N download_SRR5874681_diurnal_growth_-_dark
echo "fastq-dump --gzip --split-files ./SRR5874681_diurnal_growth_-_dark.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874681_diurnal_growth_-_dark_fqdump.out -j y -N dump_SRR5874681_diurnal_growth_-_dark -hold_jid download_SRR5874681_diurnal_growth_-_dark

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874702/SRR5874702.sra -O SRR5874702_post-germination.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874702_post-germination_download.out -j y -N download_SRR5874702_post-germination
echo "fastq-dump --gzip --split-files ./SRR5874702_post-germination.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874702_post-germination_fqdump.out -j y -N dump_SRR5874702_post-germination -hold_jid download_SRR5874702_post-germination

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874701/SRR5874701.sra -O SRR5874701_zygote.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874701_zygote_download.out -j y -N download_SRR5874701_zygote
echo "fastq-dump --gzip --split-files ./SRR5874701_zygote.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874701_zygote_fqdump.out -j y -N dump_SRR5874701_zygote -hold_jid download_SRR5874701_zygote

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874700/SRR5874700.sra -O SRR5874700_mt-_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874700_mt-_gamete_download.out -j y -N download_SRR5874700_mt-_gamete
echo "fastq-dump --gzip --split-files ./SRR5874700_mt-_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874700_mt-_gamete_fqdump.out -j y -N dump_SRR5874700_mt-_gamete -hold_jid download_SRR5874700_mt-_gamete

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874699/SRR5874699.sra -O SRR5874699_mt+_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874699_mt+_gamete_download.out -j y -N download_SRR5874699_mt+_gamete
echo "fastq-dump --gzip --split-files ./SRR5874699_mt+_gamete.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874699_mt+_gamete_fqdump.out -j y -N dump_SRR5874699_mt+_gamete -hold_jid download_SRR5874699_mt+_gamete

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874698/SRR5874698.sra -O SRR5874698_mt-_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874698_mt-_vegetative_download.out -j y -N download_SRR5874698_mt-_vegetative
echo "fastq-dump --gzip --split-files ./SRR5874698_mt-_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874698_mt-_vegetative_fqdump.out -j y -N dump_SRR5874698_mt-_vegetative -hold_jid download_SRR5874698_mt-_vegetative

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874697/SRR5874697.sra -O SRR5874697_mt+_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874697_mt+_vegetative_download.out -j y -N download_SRR5874697_mt+_vegetative
echo "fastq-dump --gzip --split-files ./SRR5874697_mt+_vegetative.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874697_mt+_vegetative_fqdump.out -j y -N dump_SRR5874697_mt+_vegetative -hold_jid download_SRR5874697_mt+_vegetative

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874694/SRR5874694.sra -O SRR5874694_media_-Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874694_media_-Cu_download.out -j y -N download_SRR5874694_media_-Cu
echo "fastq-dump --gzip --split-files ./SRR5874694_media_-Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874694_media_-Cu_fqdump.out -j y -N dump_SRR5874694_media_-Cu -hold_jid download_SRR5874694_media_-Cu

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874691/SRR5874691.sra -O SRR5874691_media_+Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874691_media_+Cu_download.out -j y -N download_SRR5874691_media_+Cu
echo "fastq-dump --gzip --split-files ./SRR5874691_media_+Cu.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874691_media_+Cu_fqdump.out -j y -N dump_SRR5874691_media_+Cu -hold_jid download_SRR5874691_media_+Cu

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874690/SRR5874690.sra -O SRR5874690_media_-Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874690_media_-Fe_-_poly-A_download.out -j y -N download_SRR5874690_media_-Fe_-_poly-A
echo "fastq-dump --gzip --split-files ./SRR5874690_media_-Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874690_media_-Fe_-_poly-A_fqdump.out -j y -N dump_SRR5874690_media_-Fe_-_poly-A -hold_jid download_SRR5874690_media_-Fe_-_poly-A

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874689/SRR5874689.sra -O SRR5874689_media_+Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874689_media_+Fe_-_poly-A_download.out -j y -N download_SRR5874689_media_+Fe_-_poly-A
echo "fastq-dump --gzip --split-files ./SRR5874689_media_+Fe_-_poly-A.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874689_media_+Fe_-_poly-A_fqdump.out -j y -N dump_SRR5874689_media_+Fe_-_poly-A -hold_jid download_SRR5874689_media_+Fe_-_poly-A

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874688/SRR5874688.sra -O SRR5874688_media_-Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874688_media_-Fe_-_RiboZero_download.out -j y -N download_SRR5874688_media_-Fe_-_RiboZero
echo "fastq-dump --gzip --split-files ./SRR5874688_media_-Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874688_media_-Fe_-_RiboZero_fqdump.out -j y -N dump_SRR5874688_media_-Fe_-_RiboZero -hold_jid download_SRR5874688_media_-Fe_-_RiboZero

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874687/SRR5874687.sra -O SRR5874687_media_+Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874687_media_+Fe_-_RiboZero_download.out -j y -N download_SRR5874687_media_+Fe_-_RiboZero
echo "fastq-dump --gzip --split-files ./SRR5874687_media_+Fe_-_RiboZero.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874687_media_+Fe_-_RiboZero_fqdump.out -j y -N dump_SRR5874687_media_+Fe_-_RiboZero -hold_jid download_SRR5874687_media_+Fe_-_RiboZero

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874684/SRR5874684.sra -O SRR5874684_diurnal_growth_-_light.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874684_diurnal_growth_-_light_download.out -j y -N download_SRR5874684_diurnal_growth_-_light
echo "fastq-dump --gzip --split-files ./SRR5874684_diurnal_growth_-_light.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874684_diurnal_growth_-_light_fqdump.out -j y -N dump_SRR5874684_diurnal_growth_-_light -hold_jid download_SRR5874684_diurnal_growth_-_light

echo "wget -nv ftp://ftp-trace.ncbi.nlm.nih.gov/sra/sra-instant/reads/ByRun/sra/SRR/SRR587/SRR5874681/SRR5874681.sra -O SRR5874681_diurnal_growth_-_dark.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=1G -o SRR5874681_diurnal_growth_-_dark_download.out -j y -N download_SRR5874681_diurnal_growth_-_dark
echo "fastq-dump --gzip --split-files ./SRR5874681_diurnal_growth_-_dark.sra" | qsub -V -cwd -q all.q -pe threads 1 -l vf=4G -o SRR5874681_diurnal_growth_-_dark_fqdump.out -j y -N dump_SRR5874681_diurnal_growth_-_dark -hold_jid download_SRR5874681_diurnal_growth_-_dark

