<template>
  <div>
    <div class="title">
      <h1>My adventures</h1>
      <i
        class="far fa-fish fa-3x"
        style="font-family: 'Font Awesome 5 Pro'"
      ></i>
    </div>
    <div
      style="
        background-color: #212529;
        padding: 15px;
        text-align: -webkit-center;
      "
    >
      <div
        class="container w-100 row row-cols-1 row-cols-sm-1 row-cols-md-4"
        style="justify-content: space-evenly; align-items: center"
      >
        <div class="col-md-2">
          <button
            type="button"
            class="btn btn-outline-primary text-nowrap me-2"
          >
            New adventure
          </button>
        </div>
        <div class="col-md-4">
          <input
            class="form-control me-2"
            type="search"
            placeholder="Search"
            aria-label="Search"
            v-model="searchText"
            v-on:keyup="searchAdventures"
            v-on:click="searchAdventures"
          />
        </div>
        <div class="col-md-4">
          <div style="display: flex">
            <span class="input-group-text">Rating</span>
            <div
              class="rating-div form-control"
              style="min-width: 135px"
              v-on:click="searchAdventures"
            >
              <div class="rating">
                <input type="radio" name="star" id="star1" value="5" />
                <label for="star1"></label>
                <input type="radio" name="star" id="star2" value="4" />
                <label for="star2"></label>
                <input type="radio" name="star" id="star3" value="3" />
                <label for="star3"></label>
                <input type="radio" name="star" id="star4" value="2" />
                <label for="star4"></label>
                <input type="radio" name="star" id="star5" value="1" />
                <label for="star5"></label>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div style="margin-top: 5%">
      <AdventureCard
        v-for="entity in searchResults"
        :key="entity.id"
        v-bind:adventureEntity="entity"
      ></AdventureCard>
    </div>
  </div>
</template>

<script>
import AdventureCard from "@/components/EntityCards/AdventureCard.vue";
import axios from "axios";
axios.defaults.baseURL = process.env.VUE_APP_URL;
export default {
  components: { AdventureCard },
  data: function () {
    return {
      clickedAdventureCardForEdit: "",
      numberOfPersons: "",
      searchText: "",
      searchResults: [],
      entities: [],
    };
  },
  mounted: function () {
    axios
      .get("/fishingAdventure/allByUser", {
        headers: {
          "Access-Control-Allow-Origin": process.env.VUE_APP_URL,
          Authorization: "Bearer " + localStorage.refreshToken,
        },
      })
      .then((res) => {
        this.searchResults = res.data;
        this.entities = res.data;
      });
  },
  methods: {
    searchAdventures: function () {
      if (this.searchText != "" && this.searchText.trim().lenght != 0) {
        let searchParts = this.searchText.trim().split(" ");

        this.searchResults = [];
        for (let entity of this.entities) {
          let matches = true;
          for (let i = 0; i < searchParts.length; i++) {
            if (
              !entity.name
                .toLocaleLowerCase()
                .includes(searchParts[i].toLocaleLowerCase()) &&
              !entity.location
                .toLocaleLowerCase()
                .includes(searchParts[i].toLocaleLowerCase()) &&
              !entity.vacationHomeOwner
                .toLocaleLowerCase()
                .includes(searchParts[i].toLocaleLowerCase())
            ) {
              matches = false;
              break;
            }
          }
          if (matches) {
            this.searchResults.push(entity);
          }
        }
      } else {
        this.searchResults = this.entities;
      }

      let stars = document.querySelector(
        "input[type='radio'][name='star']:checked"
      );
      if (stars != null && this.searchResults != null) {
        let rating;
        if (document.getElementById("star1").checked) {
          rating = 5;
        } else if (document.getElementById("star2").checked) {
          rating = 4;
        } else if (document.getElementById("star3").checked) {
          rating = 3;
        } else if (document.getElementById("star4").checked) {
          rating = 2;
        } else {
          rating = 1;
        }

        let newResults = [];
        for (let entity of this.searchResults) {
          if (parseFloat(entity.rating) >= rating) {
            newResults.push(entity);
          }
        }

        this.searchResults = newResults;
      }
    },
    editAdventure: function (event) {
      console.log(event);
    },
  },
};
</script>

<style scoped src="@/css/mycottages.css"></style>
