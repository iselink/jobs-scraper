# jobs scraper

This small java program will scrape jobs from `jobs.cz` and report any changes to the discord webhook.  
It is always good to know your opportunities.  

## Example config file

File must be named `config.json`.  
This file have the following parameters preset:   
 - job field - `IS/IT: Vývoj aplikací a systémů` and `IS/IT: Správa systémů a HW`
 - distance - 40 km radius from your region
 - education - `high` (`SŠ s maturitou`)

```json
{
	"scraper": {
		"region": "<your region>",
		"fields": [
			"200900013",
			"200900012"
		],
		"education": "high",
		"radius": 40
	},
	"webhook": "<Discord webhook>"
}
```