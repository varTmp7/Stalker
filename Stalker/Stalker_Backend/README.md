# Stalker-Backend
Questo repository contiene il backend del progetto Stalker, capitolato C5 per il corso di Ingegneria del Software, Università degli studi di Padova, A.A. 2019/2020.

# Istruzioni per l'uso
Testato su MacOS 10.15.3, Python 3.7.6, Flask 1.1.1
## Metodo classico (full locale)
1. Installare le dipendenze necessarie
    ```bash
    pip3 install -r requirements.txt
    ```
2. Configuare la shell 
    ```bash
    export FLASK_APP=stalker_backend
    export FLASK_ENV=development
    export NOT_K8S="1"
    ```
3. Avviare il container [docker](https://www.docker.com) di RethinkDB facendo il binding delle porte necessarie (**attenzione, quando il container viene killato si perdono tutti i dati all'interno**)
    ```bash
   docker run -d -p 28015:28015 -p 8080:8080 --name rethink rethinkdb
   ```
   Se non volete/riuscite ad usare docker c'è anche la possibilità di installare RethinkDB come servizio installato nel sistema (soluzione non testata)
4. Runnare il server
    ```bash
    flask run
    ```
   in caso non dovesse funzionare così provare con 
   ```bash
    python3 -m flask run
    ```
   
   per usare https avviare il serve con il comando
   ```bash
    flask run --cert=adhoc
    ```
## Tramite K8S (Kubernetes/Skaffold)
1. Avviare il tutto tramite l'utility [Skaffold](https://skaffold.dev)
    ```bash
    skaffold dev --port-forward
    ```
2. Una volta avviato (usare la Dashboard o k9s per sapere quando è tutto OK) l'app può essere raggiunta all'indirizzo `127.0.0.1:5000`

# Descrizione API
Ogni chiamata ad un API restituisce un valore (req_code) nell'header della risposta.
- **/organizations**
    - **GET**: lista di tutte le organizzazioni
        - _req_code_: 0
    - **POST**: creazione di una nuova organizzazione
        - _req_code_: 1
- **/organizations/<organization_id>**
    - **GET**: restituisce le info dell’organizzazione di id <organization_id>
        - _req_code_: 2
	- **PUT**: modifica l’organizzazione di id <organization_id>
	    - _req_code_: 3
	- **DELETE**: elimina l’organizzazione di id <organization_id>
	    - _req_code_: 4
- **/organizations/<organization_id>/places**
    - **GET**: restituisce la lista con tutte i luoghi dell’organizzazione di id <organization_id>
        - _req_code_: 5
	- **POST**: crea un nuovo luogo per l’organizzazione di id <organization_id>
	    - _req_code_: 6
- **/organizations/<organization_id>/places/<place_id>**
	- **GET**: restituisce le info del luogo di id <id_place> dell’organizzazione di id <organization_id>
	    - _req_code_: 7
	- **PUT**: modifica il luogo di id <place_id> dell’organizzazione di id <organization_id>
	    - _req_code_: 8
	- **DELETE**: elimina il luogo di id <place_id> dell’organizzazione di id <organization_id>
	    - _req_code_: 9
- **/organizations/<organization_id>/places/<place_id>/tracks**
    - **GET**: restituisce tutti i tracciamenti avvenuti per il luogo di id <place_id>
        - _req_code_: 10
    - **POST**: aggiunte un nuovo tracciamneto al luogo di id <place_id>
        - _req_code_: 11
- **/organizations/<organization_id>/tracks**
    - **GET** restituisce tutti i tracciamenti avvenuti per l'organizzazione di id <organization_id>
        - _req_code_: 12

# Struttura del progetto
- **setup.py** file con informazioni utili all'installazione del server in produzione
- **requirements.txt** lista di tutti i  pacchetti python in uso nel progetto
- **Dockerfile** file per la creazione dell'immagine Docker. [Link a DockerHub](https://hub.docker.com/repository/docker/jjocram/stalker-backend)
- **skaffold.yml** file usato da [Skaffold](https://skaffold.dev) per testare il backend su K8S in maniera rapida
- **stalker_backend.postman_collection.json** export della collection Postman usata per eseguire richieste HTTP
- **tests/** cartella contenente la suite di test usata ([pyTest](https://docs.pytest.org/en/latest/))
    - **config_test.py** fixtures da eseguire prima dei test
        - test_client: crea un client che comunica con il bakckend
        - init_db: inizializza il database (_TESTDB.sqlite_) contenente le informazioni di tutte le organizzazioni
        - init_db_organization: inizalizza il db per l'organizzazione con id=1
    - **test_models.py** batteria di test per i modelli
        - test_new_organization_public
        - test_new_organization_private
    - **test_organizations.py** batteria di test per le organizzazioni
        - test_get_organizations: verifica che le organizzazioni create da init_db siano state create correttamente
        - test_create_new_organization: verifica che una nuova organizzazione sia creata correttamente
        - test_edit_organization: verifica che il campo email di un organizzazione venga modificato correttamente
        - test_edit_organization_name: verifica che una modifica al nome di un'organizzazioni sia gestito correttamente dal sistema
        - test_delete_organization: verifica che un organizzazione venga eliminata correttamente
    - **test_places.py** batteria di test per i luoghi
        - test_get_places: verifica che i luogi creati per l'organizzazione 1 in init_db_organization vengano resituiti correttamente
        - test_create_new_place: verifica che un nuovo luogo venga creato correttamente
        - test_edit_place: verifica che un luogo venga modificato correttamente
        - test_delete_place: verifica che un luogo venga eliminato correttamente
    - **test_tracks.py** batteria di test per il tracciamento
        - test_get_tracks: verifica che i tracciamenti inserita in init_db_organization vengano restituiti correttamente
        - test_add_tracks: verifica che un nuovo tracciamento venga inserito correttamente
- **k8s/** cartella contenente i diversi file di configurazioni per il funzionamento su [Kubernetes](https://kubernetes.io)
    - **postgrtes-credentials.yml** username e password per accedere ai db di Postgres
    - **postgrtes-deployment.yml** descrizione del deployment di Postgres
    - **postgrtes-pv.yml** descrizione del Persistent Volumes usato da Postgres
    - **postgrtes-pvc.yml** descrizione del Persisten Volumes Claim usato da Postgres
    - **postgrtes-service.yml** descrizione del servizio di Postgres al quale gli altri Pods possono collegarsi
    - **rethink-pv.yml** descrizione del Persistent Volume usato da Rethink
    - **rethink-pvc.yml** descrizione del Persistent Volume Claim usato da Rethink
    - **rethink-deployment.yml** descrizione del deployment di Rethink
    - **rethink-service.yml** descrizione del servizio di Rethink al quale gli altri Pods possono collegarsi 
    - **stalker-deployment.yml** descrizione del deployment dell'app Stalker-Backend
    - **stalker-deployent-arm.yml** descrizione del deployemnt dell'app Stalker-Backend usando un immagine per dispositivi ARM based
    - **stalker-service.yml** descrizione del servzio dell'app Stalker-Backend
    - **stalker-ingress.yml** descrizione del punto di ingresso dei vari servizi che compongono l'app Stalker-Backend
- **stalker_backend/** cartella dove è contenuto il backend vero e proprio
    - **\_\_init\_\_.py** file dal quele viene avviato il servizio di backend all'esecuzione di `flask run`
        - create_app: 
            - crea l'app che viene eseguita
            - carica la configurazione
            - prova a connettersi a RethinkDB
            - crea (in caso sia assente) il database da usare su RethinkDB
            - inizializza [Flask-SQLAlchemy](https://flask-sqlalchemy.palletsprojects.com/en/2.x/)
            - inizializza [Flask-Restful](https://flask-restful.readthedocs.io/en/latest/)
            - aggiunge le _Resource_ che rispondono alle richieste HTTP
            - crea (in caso sia assente) il database per le informazioni sulle organizzazioni
            - carica il _Model_ delle organizzazioni
    - **config.py** file contenente le configurazioni standard del server flask (usa la variabile d'ambiente _NOT_K8S_ per sapere in che ambiente si trova)
    - **Models/** cartella contenente i modelli delle entità presenti nel database
        - **Organization.py** Modello delle organizzazioni 
        - **Place.py** Modello dei luoghi 
        - **Track.py** Modello per i tracciamenti
    - **Resources/** cartella contenente i metodi che rispondono alle varie richieste HTTP
        - **OrganizationList.py** operazioni sulla lista di tutte le organizzazioni
            - get: ritorna la lista di tutte le organizzazioni
            - post: esegue il parsing dei dati passati e crea una nuova organizzazione resitituendola nella risposta
        - **OrganizationItem.py** uperazioni sulla singola organizzazione
            - get(organization_id): ritorna le info dell'organizzazione di id _organization_id_
            - put(organization_id): modifica l'organizzazione di id _organization_id_ e restituisce l'organizzazione modificata
            - delete(organization_id): elimina l'organizzazione di iid _organization_id_, non restituisce nulla (controllare lo stato della risposta per sapere se è andata a buon fine)
        - **PlaceList.py** operazioni sulla lista dei luoghi di un'organizzazione
            - get(organization_id): ritorna la lista dei luoghi dell'organizzazione di id _organization_id_
            - post(organization_id): crea un nuovo lugoo per l'orgsnizzazione di id _organization_id_ e ritorna il lugo creato
        - **PlaceItem.py** operazioni sul singolo luogo
            - get(organization_id, place_id): ritorna le informazioni del lugo di in _place_id_ appartenente all'organizzazione di id _organization_id_
            - put(organization_id, place_id): modifica il luogo di id _place_id_ appartenente all'organizzazione di id _organization_id_ ritornarnando il luogo modificato
            - delete(organization_id, place_id): elimina il luogo di id _place_id_ appartenente all'organizzazione di id _organization_id_, non ritorna nulla
        - **TrackListForPlace.py**
            - get(organization_id, place_id): ritorna la lista di tutti i tracciamenti del luogo di id _place_id_ appartenente all'organizzazione di id _organization_id_
            - post(organization_id, place_id): aggiunge un nuovo tracciamento al luogo di id _place_id_ appartenente all'organizzazione di id _organization_id_, non ritorna nulla
        - **TrackListForOrganization.py**
            - get(organization_id): ritorna la lista di tutti i tracciamenti avvenuti in tutti i lughi appartenenti all'organizzazione di id _organization_id_; ogni tracciamento contiene le informazioni sul luogo dove è stato tracciato
    - **Parser/** cartella contenente i parser per la lettura dei dati nelle varie richieste
        - **OrganizationParser.py** parser per i parametri delle organizzazioni
        - **PlaceParser.py** parser per i parametri dei luoghi
        - **TrackParser.py** parser per i parametr dei tracciamenti
    - **ContentProvider/** cartella contenente i content provider per accedere a database più specifici
        - **OrganizationContentProvider.py** provider per accedere al database di una singola organizzazione (sia postgres che Rethink)
            - \_\_init\_\_(organization_name)
            - get_number_of_people(place_id)
            - add_new_track(track, entered, place_id)
            - create_new_place(place)
            - delete_place(place)
            - delete_organization
            - changed_organization_name(old_name, new_name)
           
# Databases
Al momento viene usato un database sqlite3 creato e gestito localmente a dove viene eseguito il server.
In caso venga eseguito tramite K8S viene usato un database Postgres.

Per tenere traccia del numero di persone all'interno di un lugo viene usato [RethinkDB](https://rethinkdb.com).

Esiste un database comune a tutte le organizzazioni dove è presente la tabella _organizations_. Viene poi creato un database per ogni organizzazione (con nome sha256(nome\_organizzazione)) dove sono presenti le tabelle _places_
## Tabelle Postgres
### organizations
- id: Integer, PK
- name: String(128), _unique_
- address: String(128)
- city: String(128)
- region: String(128)
- postal_code: String(5)
- nation: String(128)
- phone_number: String(16)
- email: String(128), _unique_
- organization_type: enum(private, public, both)
- ldap_url: String(2048), nullable
- ldap_port: Integer, nullable
- ldap_domain_component: String(128), nullable
- ldap_common_name: String(128), nullable
### places
- id: Integer
- name: String(128)
- first_node_latitude: Float
- firs_node_longitude: Float
- second_node_latitude: Float
- second_node_longitude: Float
- third_node_latitude: Float
- third_node_longitude: Float
- fourth_node_latitude: Float
- fourth_node_longitude: Float
- max_num_people: Integer, nullable
- approved: Bool
- organization_id: Integer
### tracks
- id: Integer
- entered: Bool
- uidNumber: Integer
- username: String(128), nullable
- name: String(128), nullable
- surname: String(128), nullable
- dateTime: DateTime
- place_id: Integer

## "Tabelle" RethinkDB
Per ogni organizzazione viene creata una "tabella" all'interno del database comune _stalker-organizations_ usando l'hash del nome dell'organizzazione come nome per l'organizzazione.
```
stalker-organizations {
    sha256(NomeOrganizzazione_1): [
        {
            'id': 'id_luogo1',
            'number_of_people': #persone
        },
        {
            'id': 'id_luogo2',
            'number_of_people': #persone
        }      
    ],
    sha256(NomeOrganizzazione_2): [
        {
            'id': 'id_luogo1',
            'number_of_people': #persone
        },
        {
            'id': 'id_luogo2',
            'number_of_people': #persone
        }      
    ]
}
```
