# Scripting

Scraper contains Rhino engine (from Mozilla) for scripting purposes.

## Examples

### Scrape
```js
'use strict';

let config = Program.loadConfig('config.json');

// Code below is only if engine is not initiated with safe objects (if I decide to change this in the future)
// Thus there is alternative way to do this.
// let jobsCzScraper = new Packages.net.iselink.jobsscraper.JobsCzScraper('praha', [''], 'high', 40);
let jobsCzScraper = Program.newScraper(
    config.getScraper().getRegion(),
    config.getScraper().getFields(),
    config.getScraper().getEducation(),
    config.getScraper().getRadius()
);

jobsCzScraper.scrape();
let comp = jobsCzScraper.compareWith('save.json');
Program.print(`added: ${comp.getAddedEntries().length}, removed: ${comp.getAddedEntries().length}`);
```

### Send POST request

```js
let wr = new WebRequest();
wr.sendPost('http://localhost:8000/', JSON.stringify({
    "xd": "xd"
}));

Program.print(wr.status_code);
```