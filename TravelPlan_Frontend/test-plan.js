import axios from 'axios';

async function testAuth() {
  try {
    const regReq = { name: "test", email: "test@test.com", password: "pwd" };
    console.log("Registering...", regReq);
    let res;
    try {
      res = await axios.post('http://localhost:8080/api/auth/register', regReq);
    } catch(err) {
      if (err.response && err.response.data === "Email already exists") {
        console.log("Already registered, logging in...");
        res = await axios.post('http://localhost:8080/api/auth/login', regReq);
      } else {
         throw err;
      }
    }
    
    const token = res.data.token;
    console.log("Got token");
    
    const planReq = { city: "Delhi", days: 3, budget: 15000, travelers: 2, foodType: "standard" };
    console.log("Generating plan...", planReq);
    
    const api = axios.create({ baseURL: 'http://localhost:8080' });
    api.interceptors.request.use(config => {
       config.headers.Authorization = `Bearer ${token}`;
       return config;
    });
    
    const planRes = await api.post('/plan', planReq);
    console.log("Success! Total cost: ", planRes.data.totalCost);
  } catch(err) {
    if (err.response) {
      console.error("Failed!", err.response.status, err.response.data);
    } else {
      console.error("Failed!", err);
    }
  }
}
testAuth();
