# jobs scraper

This small java program will scrape jobs from `jobs.cz` and report any changes to the discord webhook.  
It is always good to know your opportunities.

**JS is currently broken by changes...**

## Program arguments

Command line arguments are composed of two types - flags and commands.  
Flags for changing parameters and Commands for change in behaviour.

Possible subcommands are:

- scrape - scrape server, compare with previous data and if there is change, posts info what is added/removed into
  Discord channel via webhook.
- script - execute JS script

## Example config file

File must be named `config.json`.  
This file have the following parameters preset:

- job field - `IS/IT: Vývoj aplikací a systémů` and `IS/IT: Správa systémů a HW`
- distance - 40 km radius from your region
- education - `high` (`SŠ s maturitou`)
- scraper_prace_cz_url - URL for query on prace.cz.

Every URL must be not escaped.  
For example `%20` (escaped space) in URL is not permitted.

```json
{
  "jobsCzScraper": {
    "scrapers": {
      "scraper_job_cz": {
        "region": "<your region>",
        "fields": [
          "200900013",
          "200900012"
        ],
        "education": "high",
        "radius": 40
      },
      "scraper_prace_cz_url": "<address>"
    }
  },
  "webhook": "<Discord webhook>"
}
```