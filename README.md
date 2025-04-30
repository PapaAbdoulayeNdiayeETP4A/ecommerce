# Application E-commerce Android

Une application mobile d'e-commerce complète développée pour Android, offrant une expérience d'achat fluide avec une interface moderne et intuitive.

## Caractéristiques

- 🔐 **Authentification sécurisée** - Inscription et connexion via Firebase
- 🏠 **Page d'accueil dynamique** - Produits vedettes, nouveautés et catégories
- 🔍 **Recherche et filtrage** - Trouvez rapidement ce que vous cherchez
- 🛍️ **Catalogue complet** - Parcourez les produits par catégories
- 🛒 **Panier d'achat** - Ajoutez, modifiez ou supprimez des articles facilement
- 💳 **Processus de paiement fluide** - Plusieurs options de paiement
- 📦 **Suivi de commandes** - Historique et statut des commandes
- 👤 **Profil utilisateur** - Gérez vos informations personnelles

## Architecture

L'application est construite selon le pattern MVVM (Model-View-ViewModel) et utilise les dernières pratiques recommandées par Google pour le développement Android.

### Technologies utilisées

- **Architecture Components** - ViewModel, LiveData, Room
- **Retrofit** - Pour les appels API REST
- **Glide** - Pour le chargement et la mise en cache d'images
- **Firebase** - Pour l'authentification et les notifications
- **Material Design** - Pour une interface utilisateur moderne et cohérente
- **Stripe** - Pour le traitement des paiements

## Installation

1. Clonez ce dépôt
   ```
   git clone git@github.com:PapaAbdoulayeNdiayeETP4A/ecommerce.git
   ```

2. Ouvrez le projet dans Android Studio

3. Configurez Firebase
   - Créez un projet dans la console Firebase
   - Ajoutez votre application Android avec le package `com.example.ecommerce`
   - Téléchargez le fichier `google-services.json` et placez-le dans le dossier `/app`

4. Configurez Stripe (pour les paiements)
   - Créez un compte Stripe
   - Ajoutez votre clé API dans le fichier `api_keys.properties`

5. Exécutez l'application sur un émulateur ou un appareil

## Configuration

Pour configurer l'application avec votre propre backend, modifiez l'URL de base dans `RetrofitClient.java` :

```java
private static final String BASE_URL = "https://votre-api.com/";
```

## Structure du projet

```
com.example.ecommerce/
  ├── adapters/         // Adaptateurs pour RecyclerView
  ├── api/              // Interface API REST
  ├── database/         // Classes d'accès aux données locales (Room)
  ├── di/               // Injection de dépendances
  ├── models/           // Classes de modèles de données
  ├── repositories/     // Couche d'accès aux données
  ├── ui/               // Activités et fragments
  ├── utils/            // Classes utilitaires
  └── viewmodels/       // ViewModels pour MVVM
```

## Prérequis

- Android Studio Arctic Fox ou plus récent
- SDK Android 21 ou plus récent
- JDK 11 ou plus récent
- Compte Firebase (pour l'authentification)
- Compte Stripe (pour les paiements)

## Roadmap

- [ ] Implémentation des listes de favoris
- [ ] Avis et notations des produits
- [ ] Paiement Apple Pay / Google Pay
- [ ] Support multilingue
- [ ] Thème sombre
- [ ] Optimisation pour tablettes

## Contribution

Les contributions sont les bienvenues ! N'hésitez pas à créer une issue ou une pull request.

1. Forkez le projet
2. Créez votre branche de fonctionnalité (`git checkout -b feature/amazing-feature`)
3. Committez vos changements (`git commit -m 'Add some amazing feature'`)
4. Poussez vers la branche (`git push origin feature/amazing-feature`)
5. Ouvrez une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Remerciements

- [Android Jetpack](https://developer.android.com/jetpack)
- [Material Design](https://material.io/design)
- [Firebase](https://firebase.google.com/)
- [Retrofit](https://square.github.io/retrofit/)
- [Glide](https://github.com/bumptech/glide)