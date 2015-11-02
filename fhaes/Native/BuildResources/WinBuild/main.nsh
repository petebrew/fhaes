; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "FHAES"
!define PRODUCT_VERSION "${project.version}"
!define PRODUCT_PUBLISHER "University of Arizona"
!define PRODUCT_WEB_SITE "http://www.fhaes.org"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
!define MULTIUSER_EXECUTIONLEVEL Admin
!define PLATFORM_SUFFIX ""
!define OUTFOLDER  "Windows"
!include MultiUser.nsh

; MUI 1.67 compatible ------
!include "MUI.nsh"

; Handle file associations 
;!include "FileAssociation.nsh"
!include "fileassoc.nsh"

Function .onInit
  !insertmacro MULTIUSER_INIT
FunctionEnd

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\orange-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\orange-uninstall.ico"
!define MUI_WELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "${NSISDIR}\Contrib\Graphics\Wizard\orange-uninstall.bmp"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "../../../src/main/resources/Licenses/license.txt"
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; MUI end ------

Name "${PRODUCT_NAME}"
OutFile "..\..\fhaes-${PRODUCT_VERSION}-${PLATFORM_SUFFIX}setup-unsigned.exe"

InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show

Section "MainSection" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer
  File "..\..\fhaes-${PRODUCT_VERSION}.exe"
  File "..\..\..\src\main\resources\images\fhxfile.ico"
  
  CreateDirectory "$SMPROGRAMS\FHAES"
  CreateShortCut "$SMPROGRAMS\FHAES\FHAES.lnk" "$INSTDIR\fhaes-${PRODUCT_VERSION}.exe"
  CreateShortCut "$DESKTOP\FHAES.lnk" "$INSTDIR\fhaes-${PRODUCT_VERSION}.exe"
  !insertmacro APP_ASSOCIATE "fhx" "FHAES.textfile" "Fire history exchange file" \
     "$INSTDIR\fhxfile.ico,0" "Open with FHAES" "$INSTDIR\fhaes-${PRODUCT_VERSION}.exe $\"%1$\""
  
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\fhaes-${PRODUCT_VERSION}.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) was successfully removed from your computer."
FunctionEnd

Function un.onInit
  !insertmacro MULTIUSER_UNINIT
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name) and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\fhaes-${PRODUCT_VERSION}.exe"

  Delete "$DESKTOP\FHAES.lnk"
  Delete "$SMPROGRAMS\FHAES\FHAES.lnk"
  Delete "$INSTDIR\fhxfile.ico"

  RMDir "$SMPROGRAMS\FHAES"
  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  !insertmacro APP_UNASSOCIATE "fhx" "fhaes.textfile"
  SetAutoClose true
SectionEnd