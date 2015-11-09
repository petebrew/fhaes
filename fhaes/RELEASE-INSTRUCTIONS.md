1. Compile manual including glossary, bibTeX and makeindex commands
2. Run pdf2htmlEX to generate HTML version of manual
3. Copy html file to server and update symbolic link for most recent version
4. Update RemoteHelp URLs in FHAES code where necessary
5. Update maven version numbers in FHAES and subprojects
6. Update dependency version numbers
7. Run maven package to  generate binaries
8. Copy binaries to server
9. Update FRAMES website to show new compile date and version number
10. Change the most recent release date on download.fhaes.org to notify auto-updaters
