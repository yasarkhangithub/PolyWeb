# PolyWeb
PolyWeb is a query federation approach which can federate SPARQL queries over different data models (RDF and non-RDF data models) simultaneously.

## Experimental Setup
The experimental setup (i.e. code, datasets, setting, queries) for evaluation of PolyWeb is described here.

### Code
The PolyWeb source code can be checkedout from [PolyWeb GitHub Page](https://github.com/yasarkhangithub/PolyWeb). 

### Datasets
There are three different types of datasets used for PolyWeb evaluation experiments, i.e. RDF, RDB and TSV. RDF dataset is available in the form of Virtuoso db file, RDB dataset is available in the form of MySQL database dump file and TSV dataset is available in the form of TSV file.

These datasets can be downloaded from [PolyWeb Datasets](https://goo.gl/TkoObW).

### Settings
Each RDF dataset was loaded into a different Virtuoso (Open Source v.7.2.4.2) SPARQL endpoint SPARQL endpoint on separate physical machines. Relational data set is loaded into MySQL database (version 5.7.14) and CSV dataset into Apache Drill (version 1.8.0). All experiments are carried out on a local network, so that network cost remains negligible. The machines used for experiments have a 2.60 GHz Core i7 processor, 8 GB of RAM and 500 GB hard disk running a 64-bit Windows 7 OS. The database configuration parameters are used as default configuration for MySQL database. Default configurations are also used for Apache Drill. The configuration parameters are listed in Table 1 below.

**Table 1:** Datasets Configuration

| Dataset       | Port           | URL  | Config Parameters  |
| ------------- |-------------| -----| -----|
| COSMIC-CNV      | 8899 | {System-IP}:8899/sparql | NoB=680000, MDF=500000, MQM=8G |
| TCGA-OV-CNV      | 8085      |   jdbc:mysql://{System-IP}:8085/tcga_ov_cnv | Default configuration |
| CNVD-CNV | 8047      |    {System-IP}:8047/query.json | Default configuration |

- *NoB = NumberOfBuffers*
- *MDF = MaxDirtyBuffers*
- *MQM = MaxQueryMem*

### Queries
A total of 10 queries are designed to evaluate and compare the query federation performance of PolyWeb against FedX and HiBISCuS based on the metrics defined.

Queries used in evaluation experiments of PolyWeb can be downloaded from [PolyWeb Queries](https://goo.gl/LQG7Qb). 

### Comparison Metrics
For each query type we measured (1) the number of sources selected; (2) the average source selection time; (3) the average query execution time;  and (4) the number of results returned to assess result completeness relatively. The performance of PolyWeb, FedX and HiBISCuS are compared based on these metrics.

## Team

[Yasar Khan](https://www.insight-centre.org/users/yasar-khan)

[Antoine Zimmermann](https://www.emse.fr/~zimmermann/)

[Alokkumar Jha](https://www.insight-centre.org/users/alok-kumar)

[Dietrich Rebholz-Schuhmann](https://www.insight-centre.org/users/dietrich-rebholz-schuhmann)

[Ratnesh Sahay](https://www.insight-centre.org/users/ratnesh-sahay)
