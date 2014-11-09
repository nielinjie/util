# README


##Create Project

	cd ~/Projects
	git clone ./template *PROJECTNAME*
	cd *PROJECTNAME*
	git remote remove origin

Change project name in 

	build.sbt
	settings.gradle
	build.gradle

	
##Gen Idea

	cd *PROJECTNAME*
	sbt compile
	sbt gen-idea

##Rename Packages

Use *IDEA*


##Deploy
######Setup Project on Baidu
……
######Local
	
	mkdir deploy
	cd deploy
	git clone *APPURL* 
	cd ..
	git clone . ./deploy/none-release
	
Change *APPID* in 
	
	build.gradle
	
##Test Deploy

	gradle push --info
