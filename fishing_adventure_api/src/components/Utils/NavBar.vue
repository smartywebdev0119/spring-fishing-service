<template>
  <div>
    <nav class="navbar sticky-top navbar-expand-lg navbar-dark bg-dark">
      <div class="container-fluid">
        <a class="navbar-brand" href="/">
          <img src="@/assets/logo_white.png" alt="" width="150" height="50" />
        </a>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarScroll"
          aria-controls="navbarScroll"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarScroll">
          <ul class="navbar-nav me-auto my-2 my-lg-0 navbar-nav-scroll">
            <li class="nav-item">
              <a class="nav-link" aria-current="page" href="/">Home</a>
            </li>

            <li class="nav-item dropdown">
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="navbarScrollingDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                v-if="loggedInRole != 'ROLE_ADMIN'"
              >
                Explore
              </a>
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="navbarScrollingDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                v-else
              >
                Services
              </a>
              <ul
                class="dropdown-menu text-center dropdown-menu-dark"
                aria-labelledby="navbarScrollingDropdown"
              >
                <li><a class="dropdown-item" href="/search/boats">Boats</a></li>
                <li>
                  <a class="dropdown-item" href="/search/cottages">Cottages</a>
                </li>
                <li>
                  <a class="dropdown-item" href="/search/adventures"
                    >Adventures</a
                  >
                </li>
              </ul>
            </li>

            <li class="nav-item dropdown">
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="navbarScrollingDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
                v-if="loggedInRole == 'ROLE_CLIENT' || loggedInRole == 'ROLE_ADMIN'"
              >
                Reservations
              </a>
              <ul
                class="dropdown-menu text-center dropdown-menu-dark"
                aria-labelledby="navbarScrollingDropdown"
              >
                <li>
                  <a class="dropdown-item" href="/client/pastReservations" v-if="loggedInRole == 'ROLE_CLIENT'"
                    >Past reservations</a
                  >
                </li>
                <li>
                  <a class="dropdown-item" href="/client/currentReservations" v-if="loggedInRole == 'ROLE_CLIENT'"
                    >Current reservations</a
                  >
                </li>
                <li>
                  <a class="dropdown-item" href="/clientReviews" v-if="loggedInRole == 'ROLE_ADMIN'"
                    >Reviews</a
                  >
                </li>
                <li>
                  <a class="dropdown-item" href="/clientComplaints" v-if="loggedInRole == 'ROLE_ADMIN'"
                    >Complaints</a
                  >
                </li>
                <li>
                  <a class="dropdown-item" href="/advertiserComplaints" v-if="loggedInRole == 'ROLE_ADMIN'"
                    >Reports</a
                  >
                </li>
              </ul>
            </li>

            <li class="nav-item" v-if="loggedInRole == 'ROLE_CLIENT'">
              <a
                class="nav-link"
                aria-current="page"
                href="/client/subscriptions"
                >Subscriptions</a
              >
            </li>

            <li
              class="nav-item dropdown"
              v-if="loggedInRole == 'homeOwner'"
            ></li>
            <li
              class="nav-item dropdown"
              v-if="
                loggedInRole == 'ROLE_VACATION_HOME_OWNER' ||
                loggedInRole == 'ROLE_FISHING_INSTRUCTOR' ||
                loggedInRole == 'ROLE_BOAT_OWNER'
              "
            >
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="navbarScrollingDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                My advertisments
              </a>
              <ul
                class="dropdown-menu text-center dropdown-menu-dark"
                aria-labelledby="navbarScrollingDropdown"
              >
                <li v-if="loggedInRole == 'ROLE_VACATION_HOME_OWNER'">
                  <a class="dropdown-item" href="/cottages">Cottages</a>
                </li>
                <li v-if="loggedInRole == 'ROLE_FISHING_INSTRUCTOR'">
                  <a class="dropdown-item" href="/fishingAdventures"
                    >Fishing adventures</a
                  >
                </li>
                <li v-if="loggedInRole == 'ROLE_BOAT_OWNER'">
                  <a class="dropdown-item" href="/boats">Boats</a>
                </li>
                <li
                  v-if="
                    loggedInRole == 'ROLE_VACATION_HOME_OWNER' ||
                    loggedInRole == 'ROLE_FISHING_INSTRUCTOR' ||
                    loggedInRole == 'ROLE_BOAT_OWNER'
                  "
                >
                  <a class="dropdown-item" href="/calendar">Calander</a>
                </li>
                <li
                  v-if="
                    loggedInRole == 'ROLE_VACATION_HOME_OWNER' ||
                    loggedInRole == 'ROLE_FISHING_INSTRUCTOR' ||
                    loggedInRole == 'ROLE_BOAT_OWNER'
                  "
                >
                  <a class="dropdown-item" href="/specialOffers"
                    >Special offers</a
                  >
                </li>

                <li
                  v-if="
                    loggedInRole == 'ROLE_FISHING_INSTRUCTOR' ||
                    loggedInRole == 'ROLE_VACATION_HOME_OWNER' ||
                    loggedInRole == 'ROLE_BOAT_OWNER'
                  "
                >
                  <a class="dropdown-item" href="/reservations">Reservations</a>
                </li>
              </ul>
            </li>
            <li class="nav-item dropdown" v-if="loggedInRole == 'ROLE_ADMIN'">
              <a
                class="nav-link dropdown-toggle"
                href="#"
                id="navbarScrollingDropdown"
                role="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                Users
              </a>
              <ul
                class="dropdown-menu text-center dropdown-menu-dark"
                aria-labelledby="navbarScrollingDropdown"
              >
                  <li><a class="dropdown-item" href="/users">All users</a></li>
                  <li><a class="dropdown-item" href="/registrationRequests">Registration Requests</a></li>
                 <li><a class="dropdown-item" href="/deleteRequests">Deletion requests</a></li>
              </ul>
            </li>
          </ul>
          <div class="d-flex" v-if="loggedInRole == undefined">
            <button
              type="button"
              class="btn btn-outline-primary"
              data-bs-toggle="modal"
              data-bs-target="#LogInModal"
            >
              Register / Log In
            </button>
          </div>

          <div class="d-flex" v-if="loggedInRole != undefined">
            <button
              type="button"
              class="btn btn-outline-primary me-2"
              v-on:click="logOut"
            >
              Log out
            </button>
            <button
              type="button"
              class="btn btn-outline-primary"
              v-on:click="openProfile"
            >
              <i class="fas fa-user"></i>
            </button>
          </div>
        </div>
      </div>
    </nav>
    <register-modal></register-modal>
  </div>
</template>

<script>
import RegisterModal from "@/components/Modals/RegisterModal.vue";
import axios from "axios";
axios.defaults.baseURL = process.env.VUE_APP_URL;

export default {
  components: { "register-modal": RegisterModal },
  name: "NavBar",
  data: function () {
    return {
      loggedInRole: undefined,
    };
  },
  mounted: function () {
    if (!localStorage.jwt) {
      this.loggedInRole = undefined;
    } else {
      axios
        .get("/users/getRoleIfActivated", {
          headers: {
            "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
            Authorization: "Bearer " + localStorage.refreshToken,
          },
        })
        .then((res) => {
          if(res.data != '')
            this.loggedInRole = res.data;
          else
            window.location.href = "/";
        });
    }
  },
  methods: {
    openProfile: function () {
      window.location.href = "/profile";
    },
    logOut: function () {
      localStorage.clear();
      window.location.href = "/";
    },
  },
};
</script>

<style scoped>
.sticy-top {
  z-index: 0;
}
</style>
