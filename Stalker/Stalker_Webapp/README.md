# StalkerWebbap
##Installare le dipendeze necessarie
Se è la prima volta che usi questa versione della webapp da dopo la RQ esegui i seguenti passaggi:

1. Elimina la cartella node_modules
2. Installa le dipendenze necessarie con `npm install` all'interno della cartella Stalker-Webapp

##Usare il backend
### In locale
1. Controlla di avere Docker installato e funzionante sul tuo PC (attenzio Widnows HOME non lo supporta, se sei Windows HOME è il tuo unico sistema operativo vai alla sezione "In remoto")
2. Aggiungi le variabili d'ambiente:
    - EMAIL_PASSWORD=PasswordDellaMailDelGruppo
    - LOCAL_HTTPS=1
3. Dalla cartella del backend invoca il comando `docker-compose up --build`
4. Modifica il file `constants.ts` modificando il BASE_URL in `https://localhost`
### In remoto
1. Assicurati che la costante BASE_URL nel file `constants.ts` sia `https://stalker-be.ddns.net` 

## Avviare la webapp
Dalla cartella Stalker-Webapp invocare il comando `ng server` una volta compilato il tutto la web app sarà raggiungibile all'indirizzo [http://localhost:4200]()

## Usare la webapp
Di seguito vengono riportati le funzionalità della web app:
- Login:
    - System admin:
        - email: admin@gmail.com
        - password: password
    - Owner admin (non presente di default se il backend è stato avviato in locale):
        - email: o_a@gmail.com 
        - password: password
- Creazione admin:
    - Creazione owner admin (può farlo solo un System admin):
        - Nella sezione Administrators è possibile creare un account di tipo Owner (inserire un'email facile da ricordare perché verrà usata per il login)
        - Per la password potere usare password tanto il backend al momento non ne controlla la correttezza (ATTENZIONE IN FUTURO POTREBBE CAMBIARE)
- Creazione organizzazioni (può farlo solo un owner admin):
    - In alto a sinistra c'è l'elenco delle organizzazioni sulle quali l'admin può operare, inoltre vi è anche la possibiltà di crearne una nuova
    - Se il backend è avviato in locale è possibile usare il server ldap che si avvia assieme agli altri servizi e quindi "usare" organizzazioni di tipo BOTH e PRIVATE
        - url: ldap
        - port: 389
        - DC: dc=daf,dc=test,dc=it
        - CN: cn=users,cn=accounts
- Creazione luoghi (può falro solo un owner admin e un manager admin) 
    - Una volta selezionata l'organizzazione sulla quale operare selezionare la tab Places nella barra di sinistra e premere il pulsante New place
