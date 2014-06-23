gribeauval
==========

Simple Java Plugin Module Framework

A small framework project done in a short amount of time for a school project.
Provided an excuse to study existing plug-in and modular frameworks, as well as dig into class loaders and expand my Java skills a bit beyond the standards required for basic courseork and certification. 

There are other frameworks and specifications that serve a similar purpose, but weren't exactly what I had in mind.
The framework is currently in a "proof of concept" state. Basically functional, but not especially useful. I will hopefully get back to it at some point, optimally as part of a project that would benefit from incorporating it, as further refinement would benefit from insights gained in actual implementation. 



Work to be done:
-Fix location of Modules directory to make more sense in a full application context/allow Modules directory to be set via variable. 
-Add support for signed jars, currently framework will happily load/run any jar it finds in Modules directory, big security hole.
-Use framework in an actual project to determine actual usefulness and what features need to be implemented. 
