import store from '@/store';
import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import DataResponse from '../model/DataResponse';
import PageResponse from '../model/PageResponse';
import MessageResponse from '../model/MessageResponse';
import UserService from '@/modules/user/service/UserApi';
import ErrorResponse from '@/modules/error/model/Error';

const baseURL = import.meta.env.VITE_API;

class API {
  static #instance: API;
  #axiosInstance: AxiosInstance;

  private constructor() {
    this.#axiosInstance = axios.create({
      baseURL: `${baseURL}/api/v1`,
      withCredentials: true,
    });

    this.#axiosInstance.interceptors.request.use(config => {
      const token = store.getState().auth.accessToken;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    this.#axiosInstance.interceptors.response.use(
      response => response,
      async (error: AxiosError<ErrorResponse>) => {
        const originalRequest = error.config;

        if (error.status === 401 && originalRequest) {
          try {
            await UserService.refreshAccessToken();
            const state = store.getState();
            const newToken = state.auth.accessToken;
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
            return this.#axiosInstance(originalRequest);
          } catch (refreshError) {
            console.error('Token refresh failed:', refreshError);
            return Promise.reject(refreshError);
          }
        }
        console.error('API Error:', error);
        return Promise.reject(error);
      }
    );
  }

  public static getClient(): API {
    if (!API.#instance) {
      API.#instance = new API();
    }
    return API.#instance;
  }

  async getData<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await this.#axiosInstance.get<DataResponse<T>>(url, config);
      return response.data.item;
    } catch (error) {
      console.error('GET data request failed:', error);
      throw error;
    }
  }

  async getPageData<T>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<{ items: T[]; page: PageResponse<T>['page'] }> {
    try {
      const response = await this.#axiosInstance.get<PageResponse<T>>(url, config);
      console.log('GET page data response:', response.data);
      return {
        items: response.data.items,
        page: response.data.page,
      };
    } catch (error) {
      console.error('GET page data request failed:', error);
      throw error;
    }
  }

  async getMessage(url: string, config?: AxiosRequestConfig): Promise<string> {
    try {
      const response = await this.#axiosInstance.get<MessageResponse>(url, config);
      return response.data.message;
    } catch (error) {
      console.error('GET message request failed:', error);
      throw error;
    }
  }

  async getBlob(url: string, config?: AxiosRequestConfig): Promise<Blob> {
    try {
      const response = await this.#axiosInstance.get(url, { ...config, responseType: 'blob' });
      return response.data;
    } catch (error) {
      console.error('GET blob request failed:', error);
      throw error;
    }
  }

  async postData<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await this.#axiosInstance.post<DataResponse<T>>(url, data, config);
      return response.data.item;
    } catch (error) {
      console.error('POST data request failed:', error);
      throw error;
    }
  }

  async postPageData<T>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<{ items: T[]; page: PageResponse<T>['page'] }> {
    try {
      const response = await this.#axiosInstance.post<PageResponse<T>>(url, data, config);
      return {
        items: response.data.items,
        page: response.data.page,
      };
    } catch (error) {
      console.error('POST page data request failed:', error);
      throw error;
    }
  }

  async postMessage(url: string, data?: any, config?: AxiosRequestConfig): Promise<string> {
    try {
      const response = await this.#axiosInstance.post<MessageResponse>(url, data, config);
      return response.data.message;
    } catch (error) {
      console.error('POST message request failed:', error);
      throw error;
    }
  }

  async patchData<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    try {
      const response = await this.#axiosInstance.patch<DataResponse<T>>(url, data, config);
      return response.data.item;
    } catch (error) {
      console.error('PATCH data request failed:', error);
      throw error;
    }
  }

  async patchPageData<T>(
    url: string,
    data?: any,
    config?: AxiosRequestConfig
  ): Promise<{ items: T[]; page: PageResponse<T>['page'] }> {
    try {
      const response = await this.#axiosInstance.patch<PageResponse<T>>(url, data, config);
      return {
        items: response.data.items,
        page: response.data.page,
      };
    } catch (error) {
      console.error('PATCH page data request failed:', error);
      throw error;
    }
  }

  async patchMessage(url: string, data?: any, config?: AxiosRequestConfig): Promise<string> {
    try {
      const response = await this.#axiosInstance.patch<MessageResponse>(url, data, config);
      return response.data.message;
    } catch (error) {
      console.error('PATCH message request failed:', error);
      throw error;
    }
  }

  async deleteMessage(url: string, config?: AxiosRequestConfig): Promise<string> {
    try {
      const response = await this.#axiosInstance.delete<MessageResponse>(url, config);
      return response.data.message;
    } catch (error) {
      console.error('DELETE message request failed:', error);
      throw error;
    }
  }
}

export default API;
