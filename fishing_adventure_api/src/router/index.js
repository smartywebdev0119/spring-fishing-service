import { createRouter, createWebHistory } from 'vue-router'
const routes = [{
    path: '/',
    name: 'Home',
    component: () =>
        import ('../views/Home.vue')
}, {
    path: '/profile',
    name: 'profile',
    component: () =>
        import ('../views/Profile.vue'),
    beforeEnter: (to, from, next) => {
        if (localStorage.role != undefined) {
            next();
        } else {
            next('/');
        }
    }
}, {
    path: '/search',
    name: 'Search',
    component: () =>
        import ('../views/Search.vue'),
    children: [{
        path: '',
        name: 'SearchAll',
        component: () =>
            import ('../views/Search.vue')
    }, {
        path: 'boats',
        name: 'SearchBoats',
        component: () =>
            import ('../views/Search.vue')
    }, {
        path: 'cottages',
        name: 'SearchCottages',
        component: () =>
            import ('../views/Search.vue')
    }, {
        path: 'adventures',
        name: 'SearchAdventures',
        component: () =>
            import ('../views/Search.vue')
    }]
}, {
    path: '/cottages',
    name: 'MyCottages',
    component: () =>
        import ('../views/MyCottages.vue'),
    beforeEnter: (to, from, next) => {
        if (localStorage.role == "ROLE_VACATION_HOME_OWNER") {
            next();
        } else {
            next('/');
        }
    }
}, {
    path: '/reservations',
    name: 'HomeOwnerReservations',
    component: () =>
        import ('../views/HomeOwnerReservations.vue')
}, {
    path: '/client/currentReservations',
    name: 'CurrentClientReservations',
    component: () =>
        import ('../views/CurrentClientReservations.vue')
}, {
    path: '/client/pastReservations',
    name: 'PastClientReservations',
    component: () =>
        import ('../views/PastClientReservations.vue')
}, {
    path: '/client/subscriptions',
    name: 'ClientSubscriptions',
    component: () =>
        import ('../views/ClientSubscriptions.vue')
}, {
    path: '/specialOffers',
    name: 'HomeOwnerSpecialOffers',
    component: () =>
        import ('../views/HomeOwnerSpecialOffers.vue')
}, {
    path: '/calendar',
    name: 'HomeOwnerCalendar',
    component: () =>
        import ('../views/HomeOwnerCalendar.vue'),
    beforeEnter: (to, from, next) => {
        if (localStorage.role == "ROLE_VACATION_HOME_OWNER") {
            next();
        } else {
            next('/');
        }
    }
}, {
    path: '/fishingAdventure',
    name: 'FishingAdventure',
    component: () =>
        import ('../views/FishingAdventure.vue')
}, {
    path: '/cottage',
    name: 'CottageProfile',
    component: () =>
        import ('../views/CottageProfile.vue')
}, {
    path: '/admin/profile',
    name: 'AdminProfile',
    component: () =>
        import ('../views/AdminProfile.vue')
}, {
    path: '/users',
    name: 'AllUsers',
    component: () =>
        import ('../views/AllUsers.vue')
}, {
    path: '/registrationRequests',
    name: 'RegistrationRequests',
    component: () =>
        import ('../views/RegistrationRequests.vue')
}, {
    path: '/fishingAdventures',
    name: 'MyFishingAdventures',
    component: () =>
        import ('../views/MyFishingAdventures.vue')
}, {
    path: '/instructorReservations',
    name: 'InstructorReservations',
    component: () =>
        import ('../views/InstructorReservations.vue')
}, {
    path: '/instructorSpecialOffers',
    name: 'InstructorSpecialOffers',
    component: () =>
        import ('../views/InstructorSpecialOffers.vue')
}]
const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})
export default router