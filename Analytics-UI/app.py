import numpy as np
import matplotlib.pyplot as plt
import streamlit as st
import requests
import pandas as pd
import argparse


#@st.cache
def init():
    url = "http://172.18.0.7:9001/api/analytics"
    res = requests.get(url)
    
    if res.status_code == 200:
        data = res.json()
        #print(data)
        return data
    else:
        pass


def st_space(num=1):
    for i in range(num):
        st.write("")


def dashboard(data):
    st.title("PeaceLand Administration Dashboard")
    
    # Number of messages
    st_space(3)
    st.write(f"**_Number of Messages Received so far:_** {data['nbmessages']}")
    st_space(1)

    # Alerts per hour
    left, right = st.columns(2)
    left.write("**_Number of alerts per Hours of the day_**")
    x = np.array(data['alertsPerHour'])
    print(x)
    fig, ax = plt.subplots()
    ax.bar(x=np.array(x[:,0]).astype("str"), height=x[:,1])
    left.pyplot(fig)


    # Number of messages sent per drone
    right.write("**_Top 5 drones with most messages sent_**")
    x = np.array(data['messagesPerDrone'])
    fig, ax = plt.subplots()
    ax.bar(x=x[:,0].astype("str"), height=x[:,1])
    right.pyplot(fig)

    st_space(1)
    # alerts localisations
    st.markdown("**_Alerts localisation_**")
    x = np.array(data['cartography'])
    st.map(pd.DataFrame({"lat": x[:,0], "lon": x[:,1]}))

def main():

    parser = argparse.ArgumentParser()
    args = parser.parse_args()
    print(args)
    
    data = init()

    if data:
        dashboard(data)
    else:
        st.error("An Error Occured ! Please reload the page")
    #    st_space(3)
    #st.subheader("Valeur moyenne des transactions par mois")
    #data = average_transactionValue_perMonth(df_)
   # fig, ax= plt.subplots(figsize=(10,6))
    #ax = sns.barplot(data=data, x="mois", y="valeurs")
   # ax.set_xticklabels('Jan Fev Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.split())
    #ax.set_xticks(np.arange(12))
   # ax = plt.plot(np.arange(12), data, color="lime")
  #  st.pyplot(fig)



if __name__ == "__main__":
    main()