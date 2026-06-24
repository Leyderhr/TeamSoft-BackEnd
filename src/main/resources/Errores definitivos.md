
# Generales (Estas pueden aplicarse a varios contextos o validaciones)
- "Competence ID is required" $\rightarrow$ **`ERR_VAL_COMPETENCE_REQUIRED`**
- "Competence Importance ID is required" $\rightarrow$ **`ERR_VAL_COMPETENCE_IMPORTANCE_REQUIRED`**
- "Levels ID is required" $\rightarrow$ **`ERR_VAL_LEVEL_REQUIRED
- "Person id is required" $\rightarrow$ **`ERR_VAL_PERSON_REQUIRED`**
- "Error leyendo el archivo" $\rightarrow$ **`ERR_FILE_READ_ERROR`**
- "Error generando el archivo CSV" $\rightarrow$ **`ERR_CSV_GENERATION
- "Role evaluation id is required" $\rightarrow$ **`ERR_VAL_ROLE_EVAL_REQUIRED

# 1-CompetenceImportance
## Service

- "Competence importance not found with ID: ": **`ERR_COMP_IMPORTANCE_NOT_FOUND
- "Cannot delete competence importance because it has associated relations": **`ERR_COMP_IMPORTANCE_CANT_BE_DELETED
- "Competence importance deleted successfully": **`COMP_IMPORTANCE_SUCCESSFULLY_ DELETED
## DTO
- "Level is required and must be at least 0": **`ERR_VAL_COMP_IMPORTANCE_LEVEL
- "Significance is required and only  letters, numbers and spaces are allowed": **`ERR_VAL_COMP_IMPORTANCE_SIGNIFICANCE

# 2-Level

## DTO
- "Level is required and must be at least 0": $\rightarrow$ **`ERR_VAL_LEVELS_VALUE
- "Significance is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_LEVELS_SIGNIFICANCE`**

# 3-Cost Distance

## DTO
- "Cost distance is required and must be at least 0" $\rightarrow$ **`ERR_VAL_COST_DISTANCE_`**
- "County A is required" $\rightarrow$ **`ERR_VAL_COST_DISTANCE_COUNTY_A_REQUIRED`**
- "County B is required" $\rightarrow$ **`ERR_VAL_COST_DISTANCE_COUNTY_B_REQUIRED`**
- "County A and County B must be different" $\rightarrow$ **`ERR_VAL_COST_DISTANCE_COUNTIES_SAME

# 4-RoleLoad
## Service
- "Role Load not found with ID: " $\rightarrow$ **`ERR_ROLE_LOAD_NOT_FOUND`**
- "Cannot delete Role Load because it has associated relations" $\rightarrow$ **`ERR_ROLE_LOAD_CANT_BE_DELETED
- "Role Load deleted successfully": $\rightarrow$ **`ROLE_LOAD_SUCCESSFULLY_DELETED
- "Cannot update the load value because it is being used in one project" $\rightarrow$ **`ERR_ROLE_LOAD_CANT_BE_UPDATED
## DTO
- "Value is required and must be at least 0" $\rightarrow$ **`ERR_VAL_ROLE_LOAD_VALUE`**
- "Significance is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_ROLE_LOAD_SIGNIFICANCE`**

# 5-PersonGroup

## DTO
- "Name is required and only letters, spaces and digits are allowed" $\rightarrow$ **`ERR_VAL_PERSON_GROUP_NAME

# 6-RoleEvaluation

## DTO
- "Levels is required adn must be at least 0" $\rightarrow$ **`ERR_VAL_ROLE_EVAL_LEVEL`**
- "Significance is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_ROLE_EVAL_SIGNIFICANCE`**

# 7-ConflictIndex

## DTO
- "Description is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_CONFLICT_INDEX_DESCRIPTION`**
- "Weight is required and must be at least 0" $\rightarrow$ **`ERR_VAL_CONFLICT_INDEX_WEIGHT`**

# 8-Nacionality

## DTO
- "Country name is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_NATIONALITY_NAME
- "Demonym is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_NATIONALITY_GENTILICIO

# 9-County

## DTO
- "County name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_COUNTY_NAME`**
- "Code is required and must contain at least 1 digits" $\rightarrow$ **`ERR_VAL_COUNTY_CODE`**

# 10-Race

## DTO
- "Race name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_RACE_NAME`**

# 11-Religion

## DTO
- "Religion name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_RELIGION_NAME`**

# 12-Client

## DTO
- "Entity name is required and nly letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_CLIENT_NAME`**
- "Address is required" $\rightarrow$ **`ERR_VAL_CLIENT_ADDRESS`**
- "only contain digits, spaces, plus and hyphen and must have at least 8 digits" $\rightarrow$ **`ERR_VAL_CLIENT_PHONE`**

# 13-ProjectStructure

## DTO
- "Name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_PROJECT_STRUCTURE_NAME`**
- "Project Structure need at least one project role" $\rightarrow$ **`ERR_VAL_PROJECT_STRUCTURE_ROLES`**

# 14-Competence

## DTO
- "Competence name is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_COMPETENCE_NAME`**
- "Competence description is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_COMPETENCE_DESCRIPTION`**
- "Define if technical competency" $\rightarrow$ **`ERR_VAL_COMPETENCE_IS_TECHNICAL

# 15-Role

## DTO
- "Role name is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`ERR_VAL_ROLE_NAME`**
- "Role description is required and only letters, numbers and spaces are allowed" $\rightarrow$ **`VAL_ROLE_DESCRIPTION`**
- "Impact is required and must be at least 0" $\rightarrow$ **`ERR_VAL_ROLE_IMPACT`**
- "Is boss is required" $\rightarrow$ **`ERR_VAL_ROLE_IS_BOSS`**

# 16-Project
## Service
- "Project not found with ID: " $\rightarrow$ **`ERR_PROJECT_NOT_FOUND
- "Cannot delete project because it has associated datas" $\rightarrow$ **`ERR_PROJECT_CANT_BE_DELETED`**
- "Competence is null in role competition" $\rightarrow$ **`ERR_PROJECT_ROLE_COMPETENCE_NULL`**
- "No boss role found in project structure for project ID: " $\rightarrow$ **`ERR_PROJECT_NOT_BOSS_FOUND`**
- "Project with ID {projectId} has no cycle defined" $\rightarrow$ **`ERR_PROJECT_NO_CYCLE_DEFINED`**
- "Only projects in FINALIZED state can be closed. Current state: " $\rightarrow$ **`ERR_PROJECT_CLOSE_INVALID_STATE`**
- "Only projects in FORMED state can be finalized. Current state: " $\rightarrow$ **`ERR_PROJECT_FINALIZE_INVALID_STATE`**
- "Cycle has no associated project structure" $\rightarrow$ **`ERR_PROJECT_CYCLE_NO_STRUCTURE`**
- "Role load not found for role in project structure of project" $\rightarrow$ **`ERR_PROJECT_ROLE_NOT_FOUND_STRUCTURE`**
- "Project name already exists: " $\rightarrow$ **`ERR_PROJECT_NAME_ALREADY_EXISTS`**
- "Duplicate project names in the same request: " $\rightarrow$ **`ERR_PROJECT_BATCH_DUPLICATE_NAMES`**
- "Project structure or roles not found for project ID: " $\rightarrow$ **`ERR_PROJECT_STRUCTURE_OR_ROLES_NOT_FOUND`**
- "Project deleted successfully" $\rightarrow$ **`PROJECT_SUCCESSFULLY_DELETED`**
## DTO
- "Project name is required, maximum length is 1024 and only letters, spaces and digits are allowed" $\rightarrow$ **`ERR_VAL_PROJECT_NAME`**
- "Initial date is required" $\rightarrow$ **`ERR_VAL_PROJECT_INITIAL_DATE`**
- "Client ID is required" $\rightarrow$ **`ERR_VAL_PROJECT_CLIENT_ID`**
- "Province ID is required" $\rightarrow$ **`ERR_VAL_PROJECT_PROVINCE_ID`**
- "Project structure ID is required (for cycle creation)" $\rightarrow$ **`ERR_VAL_PROJECT_STRUCTURE_ID`**
- "Project role evaluation id is required" $\rightarrow$ **`ERR_VAL_CLOSE_PROJECT_ROLE_EVAL_ID`**
- "Boss evaluation is required" $\rightarrow$ **`ERR_VAL_CLOSE_PROJECT_BOSS_EVAL`**
# 17-ProjectRole
## Service
## DTO
- "Role ID is required" $\rightarrow$ **`ERR_VAL_PROJECT_ROLE_ID
- "Role Load ID is required" $\rightarrow$ **`ERR_VAL_PROJECT_ROLE_LOAD_ID
- "Amount of workers is required and must be at least 1" $\rightarrow$ **`ERR_VAL_PROJECT_ROLE_WORKERS

# ProjectTechCompetenceDTO
## Service
## DTO

# 18-CloseProjectDTO
## Service
## DTO
- "Boss evaluation is required" $\rightarrow$ **`ERR_VAL_CLOSE_PROJECT_BOSS_EVAL`**
- `**
# 19-RolePersonEvaluationDTO
## Service
## DTO

# 20-Person
## Service

## DTO
- "Person name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_PERSON_NAME`**
- "ID card is required and must contain at least 8 digits" $\rightarrow$ **`ERR_VAL_PERSON_ID_CARD`**
- "Surname is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_PERSON_SURNAME`**
- "Address is required" $\rightarrow$ **`ERR_VAL_PERSON_ADDRESS`**
- "Phone is required and only contain digits, spaces, plus and hyphen and must have at least 8 digits" $\rightarrow$ **`ERR_VAL_PERSON_PHONE`**
- "Status is required" $\rightarrow$ **`ERR_VAL_PERSON_STATUS`**
- "Email is required and should be valid" $\rightarrow$ **`ERR_VAL_PERSON_EMAIL`**
- "In date is required" $\rightarrow$ **`ERR_VAL_PERSON_IN_DATE`**
- "Experience is required" $\rightarrow$ **`ERR_VAL_PERSON_EXPERIENCE`**
- "Birth date is required and must be in the past" $\rightarrow$ **`ERR_VAL_PERSON_BIRTH_DATE`**
- "Person group ID is required" $\rightarrow$ **`ERR_VAL_PERSON_GROUP_ID`**
- "Person test is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST`**
- "Sex must be 'M' or 'F'" $\rightarrow$ **`ERR_VAL_PERSON_SEX`**

# 21-PersonConflictDTO
## Service
## DTO
 - "Conflict index ID is required" $\rightarrow$ **`ERR_VAL_PERSON_CONFLICT_INDEX_ID`**
- "Person conflict ID is required" $\rightarrow$ **`ERR_VAL_PERSON_CONFLICT_TARGET_ID

# 22-PersonalInterestDTO
## Service
## DTO
- "Role ID is required" $\rightarrow$ **`ERR_VAL_PERSONAL_INTEREST_ROLE_ID`**
- "Preference is required" $\rightarrow$ **`ERR_VAL_PERSONAL_INTEREST_PREFERENCE

# 23-PersonalProjectInterestDTO
## Service
## DTO
- "Project ID is required" $\rightarrow$ **`ERR_VAL_PERSONAL_PROJECT_ID`**
- "Preference is required" $\rightarrow$ **`ERR_VAL_PERSONAL_PROJECT_PREFERENCE`**

# 24-PersonTestDTO
## Service
## DTO
- "ES is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_ES`**
- "ID is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_ID`**
- "CO is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_CO`**
- "IS is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_IS`**
- "CE is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_CE`**
- "IR is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_IR`**
- "ME is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_ME`**
- "CH is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_CH`**
- "IF is required" $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_IF`**
- "MBTI test result is required and must be a valid type like 'ENFJ'" $\rightarrow$ **`ERR_VAL_PERSON_TEST_MBTI_RESULT`**
- "Belbin roles only admit P, E, or I – nothing else is allowed." $\rightarrow$ **`ERR_VAL_PERSON_TEST_BELBIN_ROLE_VALUE`**

# 25-AgeGroup
## Service

## DTO
- "Age group name is required" $\rightarrow$ **`ERR_VAL_AGE_GROUP_NAME`**
- "Maximum age is required, must be at least 0 and cannot exceed 150" $\rightarrow$ **`ERR_VAL_AGE_GROUP_MAX_AGE`**
- "Minimum age is required, must be at least 0 and cannot exceed 150" $\rightarrow$ **`ERR_VAL_AGE_GROUP_MIN_AGE`**
- "Minimum age must be less than or equal to maximum age" $\rightarrow$ **`ERR_VAL_AGE_GROUP_RANGE_INVALID`**

# 26-AlgorithmConfig
## Service
- ""Unable to read current configuration: " $\rightarrow$ **`ERR_ALGO_CONFIG_UNABLE_READ`**
- ""Unable to save configuration: " $\rightarrow$ **`ERR_ALGO_CONFIG_UNABLE_SAVE`**
## DTO
- "The initial solution must not be null" $\rightarrow$ **`ERR_VAL_ALGO_INITIAL_SOLUTION_REQUIRED`**
- "The number of trials for obtaining a person in a team role must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_TRIALS_ROLE`**
- "Operator to use must not be null" $\rightarrow$ **`ERR_VAL_ALGO_OPERATOR_REQUIRED`**
- "Type of operator must not be null" $\rightarrow$ **`ERR_VAL_ALGO_OPERATOR_TYPE_REQUIRED`**
- "The number of executions must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_EXECUTIONS`**
- "The number of iterations must not be null, must be at least 1 and cannot exceed 100000" $\rightarrow$ **`ERR_VAL_ALGO_ITERATIONS`**
- "Calculate Time must not be null" $\rightarrow$ **`ERR_VAL_ALGO_CALCULATE_TIME_REQUIRED`**
- "Validate must not be null" $\rightarrow$ **`ERR_VAL_ALGO_VALIDATE_REQUIRED`**
- "The number of trials to obtain a valid state must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_TRIALS_STATE`**
- "The number of iterations to restart Hill Climbing with Restart must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_HC_RESTART_ITER`**
- "The Tabu list size must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_TABU_SIZE`**
- "The neighborhood size of the current state for Multi‑objective Hill Climbing with Restart must not be null, must be at least 1 and cannot exceed 20" $\rightarrow$ **`ERR_VAL_ALGO_MOHC_NB_SIZE`**
- "The neighborhood distance of the current state for Multi‑objective Hill Climbing with Restart must not be null, must be at least 1 and cannot exceed 5" $\rightarrow$ **`ERR_VAL_ALGO_MOHC_NB_DIST`**
- "The multi‑objective Tabu list size must not be null, must be at least 1 and cannot exceed 100" $\rightarrow$ **`ERR_VAL_ALGO_MO_TABU_SIZE`**
# 27-User

## Service
- "One or more roles not found" $\rightarrow$ **`ERR_USER_ROLES_NOT_FOUND`**
- "Name and surname must have at least one part" $\rightarrow$ **`ERR_USER_INVALID_NAME_OR_SURNAME`**
- "User not found with ID: " $\rightarrow$ **`ERR_USER_NOT_FOUND
- "User deleted successfully" $\rightarrow$ **`USER_SUCCESSFULLY_DELETED`**
- "Password reset successfully to system default" $\rightarrow$ **`USER_PASSWORD_RESET`**
## DTO
- "Person name is required and must be at least 3 characters" $\rightarrow$ **`ERR_VAL_USER_PERSON_NAME`**
- "Surname is required and person surname must be at least 3 characters" $\rightarrow$ **`ERR_VAL_USER_SURNAME`**
- "ID card is required and must contain only digits, at least 8" $\rightarrow$ **`ERR_VAL_USER_ID_CARD`**
- "Email is required and should be valid" $\rightarrow$ **`ERR_VAL_USER_EMAIL`**
- "Enabled status is required" $\rightarrow$ **`ERR_VAL_USER_ENABLED_STATUS`**
- "At least one role is required and must be selected" $\rightarrow$ **`ERR_VAL_USER_ROLES`**
# 28-ChangePasswordDTO
## DTO
- "Current password is required" $\rightarrow$ **`ERR_VAL_CHANGE_PASSWORD_CURRENT_REQUIRED`**
- "New password is required and must be at least 6 characters" $\rightarrow$ **`ERR_VAL_CHANGE_PASSWORD_NEW`**
- "Confirm password is required" $\rightarrow$ **`ERR_VAL_CHANGE_PASSWORD_CONFIRM_REQUIRED
# 29-Auth

## Service
- "Invalid credentials or password" $\rightarrow$ **`ERR_AUTH_INVALID_CREDENTIALS`**
- "Token has expired or already used" $\rightarrow$ **`ERR_PASSWORD_RESET_TOKEN_EXPIRED_OR_USED`**
- "User not found with username: " $\rightarrow$ **`ERR_USER_NAME_NOT_FOUD`**
- "Refresh token expired. Please login again." $\rightarrow$ **`ERR_REFRESH_TOKEN_EXPIRED`**
- "Current password is incorrect" $\rightarrow$ **`ERR_AUTH_CURRENT_PASSWORD_INCORRECT
- "New password cannot be the same as current password" $\rightarrow$ **`ERR_AUTH_NEW_PASSWORD_INCORRECT
- "Refresh token has been revoked. Please login again." $\rightarrow$ **`ERR_REFRESH_TOKEN_REVOKED`**
## DTO (LoginDTO)
- "Username is required and must be between 3 and 50 characters" $\rightarrow$ **`ERR_VAL_LOGIN_USERNAME`**
- "Password is required and must be at least 6 characters" $\rightarrow$ **`ERR_VAL_LOGIN_PASSWORD`**
- "Refresh token is required" $\rightarrow$ **`ERR_VAL_LOGIN_REFRESH_TOKEN_REQUIRED`**



# 30-TeamFormation

## Service

- "La suma de los pesos de las funciones objetivo debe ser 1." $\rightarrow$ **`ERR_TEAM_FORMATION_WEIGHTS_SUM_MUST_BE_ONE`**
- "No se encontraron proyectos con los IDs proporcionados." $\rightarrow$ **`ERR_TEAM_FORMATION_PROJECTS_NOT_FOUND`**
- "No se pudo generar ninguna propuesta de miembro para el rol dado." $\rightarrow$ **`ERR_TEAM_FORMATION_NO_MEMBER_PROPOSAL`**
- "Impossible get proposal" $\rightarrow$ **`ERR_TEAM_FORMATION_IMPOSSIBLE_GET_PROPOSAL`**
- "All weights most sum one" $\rightarrow$ **`ERR_TEAM_FORMATION_WEIGHTS_SUM_MUST_BE_ONE`**
- "You can only save projects with the status CREATED" $\rightarrow$ **`ERR_TEAM_FORMATION_SAVE_INVALID_STATE`**
- "Team proposal is null" $\rightarrow$ **`ERR_TEAM_FORMATION_PROPOSAL_NULL`**
- "Project does not have an active cycle" $\rightarrow$ **`ERR_TEAM_FORMATION_NO_ACTIVE_CYCLE`**
- "Project cycle has no associated structure" $\rightarrow$ **`ERR_TEAM_FORMATION_CYCLE_NO_STRUCTURE`**
- "Project with ID: {projectsID} is not in CREATED state" $\rightarrow$ **`ERR_TEAM_FORMATION_PROJECT_NOT_CREATED`**
- "No coinciden las iteraciones" $\rightarrow$ **`ERR_TEAM_FORMATION_ITERATIONS_MISMATCH`**
## DTO

# 31-CompetenceDimension
## Service
## DTO
- "Competence Dimension name is required and only letters and spaces are allowed" $\rightarrow$ **`ERR_VAL_COMP_DIMENSION_NAME`**
# 32-CompetenceValue

## Service
## DTO

# 33-Filter
## Service
## DTO
- "Field is required" $\rightarrow$ **`ERR_VAL_FILTER_FIELD_REQUIRED`**