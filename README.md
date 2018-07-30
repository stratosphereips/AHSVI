# AHSVI
Adversarial Heuristic Search Value Iteration 

## Description
AHSVI algorithm consists of two agents called a defender and an attacker.
The defender chooses a defense measures (honeypots, thresholds, etc.) which influences the attacker's initial belief about the possible POMDP states.
The attacker then performs sequential attacks that we compute using HSVI algorithm described in [1].
The A-HSVI algorithm was initially described in [2].
