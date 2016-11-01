Scripts are executed starting from line 1 going downwards. At the end of the script, ScriptComponent is removed from the entity.

Each script line is of the format 
"[instruction] [operands]".
There are two types of instructions: movement and control flow.

Movement instructions available are:
left, right, down, up, jump, jumpright, jumpleft, sleep and land
These instruction set the corresponding variables in MovementComponent: 
"left" sets left to true and everything else to false
All movement instructions except for "land" require one operand which specifies the amount of frames that the instruction is executed for: 
"left 10" would set the "left variable in MovementComponent for 10 frames.
"Land" instruction sets "down" variable until the entity is either grounded or has bounced of an enemy. 
"Sleep" instruction stops all movement for x amount of frames.
What the instruction actually does depends on the MovementComponent of the entity, if the "jumpVelocity" is set to zero, the entity will run a "jump" instruction for the specified amount of frames, but won't actually jump.

Control flow instructions are:
if and goto
Goto has one operand - line that it will point the ScriptSystem to. Linenumbers start at 1.
If has three operands: variable, condition and value.
Variables can be xposition, yposition or grounded, which are obtained from the components of the entity at the execution time. Conditions are just <,>,<=,etc. Values are what the variable is compared to. If the condition is true, the ScriptSystem goes to the line below the "if" line, otherwise it skips the line below.
For example:
"
if xposition > 0
	right 10
left 10
"
would move entity right if xposition > 0 and left if it xposition < 0.

Decoder will ignore the tabs, so scripts can be indented.