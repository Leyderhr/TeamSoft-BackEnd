$file = "c:\Dennis-Personal\Programacion\Proyectos\Spring\TeamSoft-BackEnd\src\main\java\com\tesis\teamsoft\metaheuristics\operator\TeamFormationOperator.java"
$content = Get-Content $file -Raw

# Reemplazar limitPersonTries
$content = $content -replace 'int limitPersonTries = 100;//Integer\.parseInt\(ResourceBundle\.getBundle\("/algorithmConf"\)\.getString\("numberPersonTries"\)\);', 'int limitPersonTries = AlgorithmConfig.getInt("numberPersonTries", 100);'

# Reemplazar initialSolution en buildTeamSimultaneously
$content = $content -replace 'int role = 0;\r\n\r\n        int initialSolution = 4;//Integer\.parseInt\(ResourceBundle\.getBundle\("/algorithmConf"\)\.getString\("initialSolutionConf"\)\);', 'int role = 0;

        int initialSolution = AlgorithmConfig.getInt("initialSolutionConf", 4);'

# Reemplazar isInvalidState
$content = $content -replace '} else if \(count < 20/\*Integer\.parseInt\(ResourceBundle\.getBundle\("/algorithmConf"\)\.getString\("posibleValidateNumber"\)\)\*/\) \{', '} else if (count < AlgorithmConfig.getInt("posibleValidateNumber", 20)) {'

$content = $content -replace 'if \(count >= 20/\*Integer\.parseInt\(ResourceBundle\.getBundle\("/algorithmConf"\)\.getString\("posibleValidateNumber"\)\)\*/\) \{\r\n            Strategy\.getStrategy\(\)\.setCountCurrent\(100/\*Integer\.parseInt\(ResourceBundle\.getBundle\("/algorithmConf"\)\.getString\("iterations"\)\)\*/\);', 'if (count >= AlgorithmConfig.getInt("posibleValidateNumber", 20)) {
            int iterations = AlgorithmConfig.getInt("iterations", 100);
            Strategy.getStrategy().setCountCurrent(iterations);'

Set-Content $file $content
Write-Host "Reemplazos completados en TeamFormationOperator.java"
