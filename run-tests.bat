@echo off
echo Running tests and generating Allure trend data...

rem Number of test runs to perform
set NUM_RUNS=5

for /L %%i in (1,1,%NUM_RUNS%) do (
   echo Running test iteration %%i of %NUM_RUNS%...
   call mvn clean test allure:report
   echo Test run %%i completed.

   rem Optional: Add a small delay between runs
   timeout /t 2 /nobreak
)

echo All test runs completed. Opening Allure report...
call mvn allure:serve