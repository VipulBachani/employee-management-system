# employee-management-system
solution to the following coding challenge provided by workmotion: https://github.com/workmotion/backend-challenge/blob/main/SENIOR_CHALLENGE.md

The employees on this system are assigned to different states.

The states (State machine) for A given Employee are:

ADDED
IN-CHECK
APPROVED
ACTIVE
Initially when an employee is added it will be assigned "ADDED" state automatically.

The allowed state transitions are:

ADDED -> IN-CHECK *-> APPROVED -> ACTIVE

Furthermore, IN-CHECK state is special and has the following orthogonal child substates:

SECURITY_CHECK_STARTED

SECURITY_CHECK_FINISHED

WORK_PERMIT_CHECK_STARTED

WORK_PERMIT_CHECK_FINISHED

with allowed state transitions:
SECURITY_CHECK_STARTED -> SECURITY_CHECK_FINISHED
WORK_PERMIT_CHECK_STARTED -> WORK_PERMIT_CHECK_FINISHED

This means that a complete state of an employee in the IN_CHECK state could look like (IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED).

Examples of permitted transition:

(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED) -> (IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_STARTED)
(IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_STARTED) -> (IN_CHECK, SECURITY_CHECK_STARTED, WORK_PERMIT_CHECK_FINISHED)
Transition from IN_CHECK state to APPROVED state happens automatically when the complete state is (IN_CHECK, SECURITY_CHECK_FINISHED, WORK_PERMIT_CHECK_FINISHED). Transition from IN_CHECK state to APPROVED without meeting the condition above is not allowed.

Here is an example how a sequence of requests might look like:

create employee
Update state of an employee to IN_CHECK
Update substate of IN_CHECK state of an employee to SECURITY_CHECK_FINISHED
Update substate of IN_CHECK state an employee to WORK_PERMIT_CHECK_FINISHED (employee is auto-transitioned to APPROVED state)
Update state of an employee to ACTIVE
Another possible sequence of requests is:

create employee
Update state of an employee to IN_CHECK
Update substate of IN_CHECK state an employee to WORK_PERMIT_CHECK_FINISHED
Update substate of IN_CHECK state an employee to SECURITY_CHECK_FINISHED (employee is auto-transitioned to APPROVED state)
Update state of an employee to ACTIVE

Your task is to build Restful API doing the following:
1. An Endpoint to support adding an employee with very basic employee details including (name, contract information, age, you can decide.) With initial state "ADDED" which indicates that the employee isn't active yet.
2. Another endpoint to change the state of a given employee to any of the states defined above in the state machine respecting the transition rules
3. An Endpoint to fetch employee details
