TEMPLATE FOR RETROSPECTIVE (Team ##)
=====================================

The retrospective should include _at least_ the following
sections:

- [process measures](#process-measures)
- [quality measures](#quality-measures)
- [general assessment](#assessment)

## PROCESS MEASURES 

### Macro statistics

- Number of stories committed vs done: 8 committed, 8 done
- Total points committed vs done: 23 committed, 23 done 
- Nr of hours planned vs spent (as a team): 112h planned, 112h 15m spent

**Remember**  a story is done ONLY if it fits the Definition of Done:
 
- Unit Tests passing
- Code review completed
- Code present on VCS
- End-to-End tests performed

### Detailed statistics

| Story  | # Tasks | Points | Hours est. | Hours actual |
|--------|---------|--------|------------|--------------|
| _#0_   | 25      | -      | 67h 30m    | 66h 41       |
| TM-26  | 5       | 5      | 10h        | 9h           |
| TM-15  | 2       | 5      | 4h         | 3h           |
| TM-27  | 5       | 3      | 10h        | 9h 30m       |
| TM-16  | 5       | 3      | 8h         | 8h           |
| TM-28  | 5       | 3      | 8h         | 9h           |
| TM-29  | 5       | 2      | 2h 30m     | 2h 15m       |
| TM-17* | 0       | 0      | 0          | 0            |
| TM-18  | 2       | 2      | 2h         | 2h           |
   
*this story was already completed on the last sprint but it wasn't reported because we didn't notice that we had done it

- Estimated average hours per task: **2h 04m**
- Actual average hours per task: **2h 05m**
- Estimated average hours per task std. deviation: **2.4**
- Actual average hours per task std. deviation: **1.98**
- Total task estimation error ratio (sum of total hours estimation / sum of total hours spent - 1): **-0.002**

  
## QUALITY MEASURES 

- Unit Testing:
  - Total hours estimated: 5h
  - Total hours spent: 5h
  - Nr of automated unit test cases: 45
  - Coverage (if available): 81.9 %
- E2E testing:
  - Total hours estimated: 4h
  - Total hours spent: 4h
- Code review 
  - Total hours estimated: 6h 30m
  - Total hours spent: 8h
- Technical Debt management:
  - Total hours estimated: 3h
  - Total hours spent: 2h 55m
  - Hours estimated for remediation by SonarQube: 4h
  - Hours estimated for remediation by SonarQube only for the selected and planned issues: 4h
  - Hours spent on remediation: 2h 55m
  - debt ratio (as reported by SonarQube under "Measures-Maintainability"): 0.6%
  - rating for each quality characteristic reported in SonarQube under "Measures" (namely reliability, security, maintainability )
     - RELIABILITY: A
     - MAINTAINABILITY: A
     - SECURITY: A
  


## ASSESSMENT

- What caused your errors in estimation (if any)? Our estimation was really accurate, on average every task took as long as we expected

- What lessons did you learn (both positive and negative) in this sprint? We learned that writing more tests also speeds up the development of new features. As negative we need to be more careful about the updates made by the product owner on the stories. 

- Which improvement goals set in the previous retrospective were you able to achieve? 
  - We were able to stick to our time budget almost perfectly. 
  - We improved our test development process.
  - We better managed the user authentication during the demo presentation.
  
- Which ones you were not able to achieve? Why? None

- Improvement goals for the next sprint and how to achieve them (technical tasks, team coordination, etc.)
 - Read the FAQ document made by the product owner more frequently in order to adapt to the changes.
 - We want to improve the way we manage technical debt.

- One thing you are proud of as a Team!! 