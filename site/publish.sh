#!/bin/sh

git checkout master
git branch -D gh-pages
git push origin :gh-pages
git checkout -b gh-pages
git rm -r --cached .

>.gitignore
echo "# Exceppt Javadoc" >>.gitignore
echo "site/*" >>.gitignore

cat site/projects.txt | while read project
do
  echo "${project}/*" >>.gitignore 
  echo "!${project}/docs" >>.gitignore 
  echo "!${project}/README.md" >>.gitignore 
  cd ${project}
  mvn site
  cd -
done

git add .
git commit -m "Publish Javadoc"
git push --set-upstream origin gh-pages

git checkout master
