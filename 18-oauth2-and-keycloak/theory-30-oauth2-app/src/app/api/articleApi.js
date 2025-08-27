import axios from "axios";
import { API_PREFIX } from "@/constants/appConstant";

// Frontend App request to Backend API
export async function selectArticles() {
  const response = await axios.get(`${API_PREFIX}/articles`);
  return response?.data;
}

export async function selectArticleById(articleId) {
  const response = await axios.get(`${API_PREFIX}/articles/${articleId}`);
  return response?.data;
}

export async function createArticle(data) {
  const response = await axios.post(`${API_PREFIX}/articles`, data);
  return response.data;
}

export async function updateArticle(data) {
  const response = await axios.put(
    `${API_PREFIX}/articles/${data.articleId}`,
    data
  );
  return response.data;
}

export async function deleteArticle(id) {
  const response = await axios.delete(`${API_PREFIX}/articles/${id}`);
  return response.data;
}
