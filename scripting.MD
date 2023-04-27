# Scripting

Scraper contains Rhino engine (from Mozilla) for scripting purposes.

Example script file:

```js
'use strict';

let config = Program.loadConfig('config.json');

// Code below is only if engine is not initiated with safe objects (if I decide to change this in the future)
// Thus there is alternative way to do this.
// let scraper = new Packages.net.iselink.jobsscraper.Scraper('praha', [''], 'high', 40);
let scraper = Program.newScraper(
    config.getScraper().getRegion(),
    config.getScraper().getFields(),
    config.getScraper().getEducation(),
    config.getScraper().getRadius()
);

scraper.scrape();
let comp = scraper.compareWith('save.json');
Program.print(`added: ${comp.getAddedEntries().length}, removed: ${comp.getAddedEntries().length}`);
```