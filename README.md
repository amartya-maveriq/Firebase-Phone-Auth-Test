# Firebase-Phone-Auth-Test

While incorporating Firebase Phone Auth in one of my projects I noticed that Firebase allows new users to login using their phone number. However, this feature is not useful in case one likes to store the new user's other information using create account. Because anyone and everyone not enlisted in the database (e.g, firestore) can gain access to the app by just using their phone number.

This project helps to solve that.

There are 2 flows : 
1. create account
2. login

In case a new user tries to login using their phone number the entry will be prohibited and his created account will get deleted. Once he creates his account from the create account portal then only he can be allowed to login later.

WARNING: Written in a very crude manner just to get the idea, pls don't judge.
