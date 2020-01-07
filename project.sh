#===================================================================
# general syntax:
#  group               artifact                version     ext flags
#===================================================================
# flags:
#       j = jar
#       d = javadoc
#       s = sources
#       t = test
#===================================================================
artifacts=(
  "org.modelingvalue   dclareForJava           0.0.3       jar jds"
)
dependencies=(
  "org.modelingvalue   dclare                  0.0.6       jar jds-"
  "org.modelingvalue   immutable-collections   1.0.15      jar jds-"

  "junit               junit                   4.12        jar jdst"
  "org.hamcrest        hamcrest-core           1.3         jar jdst"
)
