# reservation-app
Ndetrimi i nje application per rezervim takimesh (Klinik Dentare)

Funksionet:
1.User Publik:(per tu identifkuar te perdoret NID,email, tel)
1.1.mund te shikoje listen e orareve te lira per rezervim per max 7 dite te ardheshme.
1.2.mund te rezervoje nje orar qe eshte i lire dhe te perzgjedh Mjekun(opsionale).
1.3.nund te anuloj nje rezervim por vetem nese eshte me shume se nje dite para takimit.
1.4.mund te shikoj takimet aktive, te refuzuara, te mbylluara.
1.5.mund te aprovoje ndryshimin e mjekut, ose anulon takimin.
2.Sekretaria e Klinikes
2.1.shikon oraret e lira, kerkesat per rezervimet e kryera, rezervimet e aprovuar, rezervimet e aprovuara.
2.2.mund ti aprovoje, ose mund ti sygjeroje nje orar tjeter te pershtatshem per rezervimin e kryer.
2.3.mund te ndryshoje mjekun ne nje takim te planifikuar.
2.4 kalon ne statusin done takimin, pra kur takim kryhet sipas planifikimit dhe mjeku ka dhene feedback.
3.Mjek Stomatolog
3.1 Shikon oraret e lira te veta, rezervimet e konfirmuara, te anuluara.
3.2 Anulon rezervimin jo me vone se 1 dite nga takimi.
3.3. Ploteson, permbledhjen e vizites.
3.4 Kerkon me te dhenat klientit dhe shikon historikun e vizitave
4. Raporte & statistika
4.1 Sa rezervime ne total  jane kryer ne jave/muaj, sa prej tyre jane anuluar nga klientet, nga Mjeket, sa jane done.
4.2 Lista e mjekeve me me shume vizita te kryera ne jave, muaj e rendituar ne rend zbrites.
4.3 Lista e mjekeve me me shume vizita te anuluara ne jave, muaj e rendituar ne rend zbrites.
5.Funksione te Automatizuar
5.1 Nese vizita kryehet dhe mjeku nuk jep feedback, ne menyre automatike pas 8H vizita do plotesohet me nje feedback default.



(Secili nga rolet, duhet te aksesoj sherbimet qe jane percaktuar per rolin ne fjale, rolet Sekretar dhe Mjek duhet te log-hen para se te aksesojne sherbimet, auth te behet me JWT).
Application duhet te jete i mire organizuar ne layers bazuar ne qellimet qe ato kane.



Techs & Frameworks:
Postgresql, Java 8, Spring Boot, Spring Data JPA,  Rest, Lombok, e te tjera
