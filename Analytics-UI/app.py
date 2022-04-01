from re import sub
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import streamlit as st
import time
import datetime

def log(function):
    def modified_function(df):
        timestamp = time.time()
        res = function(df)
        time_ = time.time()-timestamp
        with open("log.txt","a") as f:
            f.write(f"{function.__name__}: execution time = {round(time_,3)} s     call timestamp = {round(timestamp,3)} s\n")
        return res
    return modified_function

@log
@st.cache
def load_data(year):
    df = pd.read_csv(f"dataset/min_{year}.csv")
    return df

@log
@st.cache
def preprocess(df_):
    df = df_.copy()
    df.adresse_code_voie=df.adresse_code_voie.astype(str)
    df.code_commune=df.code_commune.astype(str)
    df.code_departement=df.code_departement.astype(str)
    df.numero_volume=df.numero_volume.astype(str)
    df.lot1_numero=df.lot1_numero.astype(str)
    df.lot2_numero=df.lot2_numero.astype(str)
    df.lot3_numero=df.lot3_numero.astype(str)
    df.lot4_numero=df.lot4_numero.astype(str)
    df.lot5_numero=df.lot5_numero.astype(str)
    df.date_mutation=pd.to_datetime(df.date_mutation)
    df["prix/m^2"] = df.valeur_fonciere/df.surface_terrain
    df.index = df.date_mutation
    return df


@log
def get_communs(df):
    return df.nom_commune.value_counts().index


@log
def get_types_local(df):
    return df.type_local.value_counts().index


@log
@st.cache
def lowest_square_meters_prices(values):
    df, limits = values
    b = df[(df.surface_terrain >= limits[0]) & (df.surface_terrain <= limits[1]) & ~(df["prix/m^2"].isna())]
    b = b[["nom_commune","prix/m^2"]].groupby("nom_commune").mean().sort_values(by="prix/m^2", ascending=True)[:15]
    b["nom_commune"] = b.index
    return b


@log
@st.cache
def type_location_by_communs(values):
    df, commune = values
    vals = df[df.nom_commune == commune].type_local.value_counts()
    labels = vals.index
    return (vals,labels)

@log
@st.cache
def cities_biggest_price_increase(df):
    years = []
    for i in range(4):
        years.append(preprocess(load_data(2020-i)))
    df = pd.concat(years, axis=0)
    df = df[["code_departement", "valeur_fonciere"]].groupby("code_departement").max()-df[["code_departement", "valeur_fonciere"]].groupby("code_departement").min()
    df["code"] = df.index
    df.rename(columns = {"valeur_fonciere": "augmentation_valeur_fonciere"}, inplace=True)
    return df
    

@log
@st.cache
def communs_higher_numof_transactions(df):
    a = df[["id_mutation", "nom_commune"]].groupby("nom_commune").count()
    a = a.sort_values(by="id_mutation", ascending=False)[:15]
    #a = pd.DataFrame({"number_of_transactions": a.id_mutation.values})
    return a


@log
@st.cache
def average_transactionValue_perMonth(df):
    data = df.loc[:,"valeur_fonciere"].resample("M").mean()
    return data

@log
@st.cache
def get_points_by_months(values):
    df, start_date, end_date = values
    points = df[["valeur_fonciere","latitude","longitude"]]

    points = points.loc[f"{start_date}": f"{end_date}",:]
    return points[~(points.latitude.isna() & points.longitude.isna())][["latitude","longitude"]]


@st.cache
def month_to_index(month):
    months = {"Janvier": 1, "Février": 2, "Mars": 3, "Avril":4, "Mai":5, "Juin":6, "Juillet":7, "Aout":8, "Septembre": 9, "Octobre":10, "Novembre":11, "Décembre":12}
    return months[month]

def st_space(num=1):
    for i in range(num):
        st.write("")

def sidebar():
    st.sidebar.subheader("Visualisez les transactions de l'année de votre choix")
    dataset_year = st.sidebar.selectbox(
        "Choisissez l'année souhaitée",
        ('2020','2019','2018','2017','2016'))
    return dataset_year


def someUtils():
    left_column, right_column = st.columns(2)
    # You can use a column just like st.sidebar:
    with left_column:
        st.button("click")

    # Or even better, call Streamlit functions inside a "with" block:
    with right_column:
        chosen = st.radio(
            'Sorting hat',
            ("Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin"))
        st.write(f"You are in {chosen} house!")
    
    values = st.slider(
    'Select a range of values',
    0.0, 100.0, (25.0, 75.0))
    st.write('Values:', values)

def main():

    year = sidebar()
    df1 = load_data(year)
    df_ = preprocess(df1)

    col1, col2 = st.columns(2)
    col1.image("https://media.lesechos.com/api/v1/images/view/5d1473758fe56f63c2439d98/1280x720/2202973-immobilier-faut-il-acheter-aujourdhui-web-tete-0302190155223.jpg")
    col2.image("https://upload.wikimedia.org/wikipedia/fr/thumb/3/38/Logo_de_la_R%C3%A9publique_fran%C3%A7aise_%281999%29.svg/1200px-Logo_de_la_R%C3%A9publique_fran%C3%A7aise_%281999%29.svg.png")
    st_space(3)

    st.markdown("# Transactions immobilières en France sur les 5 dernières années")
    st_space()
    st.write("Le présent jeu de données « Demandes de valeurs foncières », publié et produit par la direction générale des finances publiques, permet de connaître les transactions immobilières intervenues au cours des cinq dernières années sur le territoire métropolitain et les DOM-TOM, à l’exception de l’Alsace, de la Moselle et de Mayotte. Les données contenues sont issues des actes notariés et des informations cadastrales", width=1)
    
    # Localisation des transactions immobilières par période de l'année
    st.subheader("Localisation des transactions immobilières selon la période de l'année")
    col1, col2 =  st.columns(2)
    min_date = datetime.date(df_.index.min().year, df_.index.min().month, df_.index.min().day)
    max_date = datetime.date(df_.index.max().year, df_.index.max().month, df_.index.max().day)
    start_date = col1.date_input('Date de debut', min_date, min_date, max_date)
    end_date = col2.date_input('Date de fin', max_date, min_date, max_date)
    points = get_points_by_months((df_,start_date, end_date))
    st.write(f"{len(points)} transactions dans cette période")
    st.map(points)


    # Valeur moyenne des transactions par mois
    st_space(3)
    st.subheader("Valeur moyenne des transactions par mois")
    data = average_transactionValue_perMonth(df_)
    fig, ax= plt.subplots(figsize=(10,6))
    #ax = sns.barplot(data=data, x="mois", y="valeurs")
    ax.set_xticklabels('Jan Fev Mar Apr May Jun Jul Aug Sep Oct Nov Dec'.split())
    ax.set_xticks(np.arange(12))
    ax = plt.plot(np.arange(12), data, color="lime")
    st.pyplot(fig)


    # les communes avec le plus grand nombre de transactions
    st_space(3)
    st.subheader("Les communes avec le plus grand nombre de transactions")
    data = communs_higher_numof_transactions(df_)
    st.bar_chart(data, height=500)


    # les départements qui ont connu les plus grandes augmentations de prix au cours des 4 dernières années
    st_space(3)
    st.subheader("les départements qui ont connu les plus grandes augmentations de prix au cours des 4 dernières années")
    data = cities_biggest_price_increase(df_)
    fig, ax = plt.subplots(figsize=(10,6))
    ax = sns.barplot(data=data, x="code", y="augmentation_valeur_fonciere")
    st.pyplot(fig)


    # Les types de location les plus demandées selon les communes
    st_space(3)
    st.subheader("Les types de location les plus demandées selon les communes")

    fig, ax = plt.subplots(figsize=(10,6))
    commune=st.selectbox(
            "Choissisez une commune",
            tuple(get_communs(df_))
        )
    data, labels = type_location_by_communs((df_,commune))
    ax = plt.pie(data, labels=labels, autopct="%.0f%%")
    st.pyplot(fig)


    # Les communes avec les prix moyens au mètre carré les plus bas (en fonction du type de logement de la superficie)
    st_space(3)
    st.subheader("Les communes avec les prix moyens au mètre carré les plus bas (en fonction du type de logement et de la superficie)")
    col1, col2 = st.columns(2)
    location = col1.selectbox("Choissisez un type de logement", tuple(get_types_local(df_)))
    subdf = df_[df_.type_local == location]
    limits = (subdf.surface_terrain.min(), subdf.surface_terrain.max())
    limits = col2.slider('Sélectionnez un intervalle de valeurs',limits[0],limits[1], (20.0, 10000.0))
    data = lowest_square_meters_prices((subdf, limits))
    fig, ax = plt.subplots(figsize=(10,6))
    ax = sns.barplot(data=data, x="nom_commune", y="prix/m^2")
    ax.set_xticklabels(ax.get_xticklabels(),rotation = 50)
    st.pyplot(fig)
    #someUtils()

if __name__ == "__main__":
    main()