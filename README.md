# Report Indexer

A search engine for news reports powered by Lucene.  
The project is similar to [Lucence-Example](https://github.com/PointerFLY/Lucene-Example), check it out if necessary.


## Data Chosen 

* [News Reports](
https://drive.google.com/a/tcd.ie/uc?export=download&confirm=rjCk&id=1MudJity9Ckh8jxapFx3OS-DLEkcvbYYx)  
Each sub-folder within this dataset contains a document collection from different sources: Financial Times Limited, Federal Register (1994), Foreign Broadcast Information Service (1996), Los Angeles Times (1989, 1990).

* [Topics](https://www.dropbox.com/s/277vn6l23z2e6ku/CS7IS3-Assignment2-Topics.gz?dl=1)  
Assume a topic is one user's query, a search engine needs to find the best match report for a topic.

* qrels  
The file can be found under temp folder. It's an evaluation baseline file, in a format of [trec_eval](https://github.com/usnistgov/trec_eval). 


## How to run

[Gradle](https://gradle.org) is used thoroughly in this project. Check out build.gradle by yourself if you have knowledge on Gradle.  
I recommend open it with IDE who supports Gradle, such as [Intellij IDEA](https://www.jetbrains.com/idea/).

```bash
# Make sure gradle is installed.
git clone https://github.com/PointerFLY/Report-Indexer.git
cd Report-Indexer
gradle run
```

Since there's some problems with Google Drive when downloading a big file, it may prompt an error when executing. In that case, please download **News Reports** file by yourself based on the url provided, rename it and put it in the right place (I think you are smart enough to figure out where to put it in by looking at FileUtils.java).

After gradle run, a results.output file will be generated. Use [trec_eval](https://github.com/usnistgov/trec_eval) to evaluate it properly.

```bash
cd temp
# Mean Average Precision, Recall and other metrics
trec_eval -m map qrels results.output
trec_eval -m recall qrels results.output
trec_eval qrels results.output
```

## Performance

|Metric|Result|
|:--:|:--:|
|Mean Average Precision|0.2847|
|Recall 5|  0.0778 |
|Recall 10| 	0.1269 |
|Recall 20| 0.1965 |
|Recall 100| 0.3955 |




