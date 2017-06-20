# Helpify

![alt text](https://github.com/MReintop/HelpifyApp/blob/master/login.png)
![alt text](https://github.com/MReintop/HelpifyApp/blob/master/helpRequest.png)

### Eesmärk

Põhieesmärk: Kasutada digitaalseid tööriistu pärismaailmas ja muuta need reaalseks, mõnikord mõõdetavateks oskusteks.

### Kirjeldus

Lühikirjeldus: Inimesed saavad luua kaardi peal missioone. Teised inimesed saavad antud missioone aksepteerida 
ja aidata antud inimest. /* Teisi inimesi aidates ja missioone luues saavad inimesed oskuste alla oskuspunkte
ja kogemuspunkte, mis teevad avatari kaardil suuremaks.*/


### Viide instituudile ja paar sõna, et mille raames projekt loodud
Helpify projekt on tehtud selle raames, et Digitehnoloogiate instituudi magistri tasemel õppival tudengil on vaja järgmiseks aastaks magistri tööks projekt teha, meie osaleme projektis tarkvara arenduse praktika raames.


### Kasutatud tehnoloogiad ja nende versioonid

* Android Studio 2.3
* Firebase
* Git


### Autorid

* Alar Aasa
* Kristjan Liiva
* Mariam Reintop
* Martin Holtsmeier
* Stanislav Majevski


### Paigaldusjuhised

**Download ja Install Android Studio.**
Alla laadida Android Studio aadressil https://developer.android.com/studio/index.html
Installida Anrdoid Studio.

**Git repository clone**
Kloonida git repository leheküljelt
https://github.com/MReintop/HelpifyApp

**Update Google Repositorys.**
Avada Android Studio.
Jälgida, et Google Repository oleks up to date klõpsates nupule SDK Manager ülevalt menüüribalt.
Klõpsata vahelehele SDK Tools. Vaadata, et Support Repository alt oleks Google Repository Status Installed.
Kui ei ole siis teha linnuke Google Repository ees ja klõpsata nupule OK.

![alt text](https://github.com/MReintop/HelpifyApp/blob/master/googleRepositoryUpdate.png)


**Update other dependencies.**
Avada kaust app kohast, kuhu sai git repository kloonitud.
Antud kaustast avada build.gradle fail.
Veenduda, et kõik dependencies oleks up to date Android Studios.
Vajadusel tuleb installeerida erinevaid sõltuvusi/pakette, millest Android Studio annab märku ning pakub ka lingi selle jaoks.

**Rakenduse tööle panek.**
Telefonis enable developer mode arendaja seadete alt. Kui arendaja seadeid pole, siis telefoni tarkvara detailides vajutage järgu numbrile 7 korda. Panna telefon usb kaabli abil arvutile järgi. Vajutada Android studio ülevalt Run ja valida telefon.

Kui Androidi telefon puudub, saab Android Studios kasutada ka emulaatorit.

Rakenduse tööle panekuks vajutage "Run" nupule ning valige seade või emulaator. Kui ühtegi seadet või emulaatorit pole, vajutage "Create New Virtual Device", "Next", Nougat kõrval vajutage "Download", "Next", "Finsh", "Run". Arvutil võib vaja olla ka HAXM pakette, mida Android Studio võimaldab kergelt alla laadida. Emulaatori seaded võivad jääda vaikimisi. Pärast seda avaneb Androidi emulaator ning selles rakendus.

![alt text](https://github.com/MReintop/HelpifyApp/blob/master/runAppInPhone.png)
 Õpetus: https://developer.android.com/studio/run/managing-avds.html

**Avada projekti firebase andmebaas.**
Minna firebase lehele. Logida sisse emailiga helpifytest@gmail.com ja parooliga Testtest. Valida projekt Helpify.


## Litsents
https://github.com/MReintop/HelpifyApp/blob/master/LICENCE.txt
