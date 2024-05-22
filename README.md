#### Statistics of the lexicon
- totalWordsCount=412073
- tokensCount=18974
- largestFrequency=29385 (see `results/lexicon.csv`)

#### Datastructure chosen for lexicon implementation - `ConcurrentHashMap`
- Thread-Safe Operations and Concurrent Iteration allows to add terms using paralell streams
- O(1) Complexity for put, get, containsKey methods

#### Improvement directions
- Add some other tokenizer/preprocessor or configure `Stanford CoreNLP` to better tokenize (remove stopwords, symbols, convert to lower case, etc.)

#### Serialization
- preffered option is to convert into `csv`, as it gives more flexibility and in my case presents a lower memory usage, yet in this case we must update Lexicon class if we want to use such `csv` to load lexicon from disc
  
